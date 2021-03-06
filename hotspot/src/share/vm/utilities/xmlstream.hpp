#ifdef USE_PRAGMA_IDENT_HDR
#pragma ident "@(#)xmlstream.hpp	1.5 04/01/12 10:02:34 JVM"
#endif
/*
 * Copyright 2004 Sun Microsystems, Inc.  All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL.  Use is subject to license terms.
 */

class xmlStream;
class defaultStream;

// Sub-stream for writing quoted text, as opposed to markup.
// Characters written to this stream are subject to quoting,
// as '<' => "&lt;", etc.
class xmlTextStream : public outputStream {
  friend class xmlStream;
  friend class defaultStream; // tty
 private:

  xmlStream* _outer_xmlStream;

  xmlTextStream() { _outer_xmlStream = NULL; }

 public:
   virtual void flush(); // _outer.flush();
   virtual void write(const char* str, size_t len); // _outer->write_text()
};


// Output stream for writing XML-structured logs.
// To write markup, use special calls elem, head/tail, etc.
// Use the xmlStream::text() stream to write unmarked text.
// Text written that way will be quoted as necessary using '&lt;', etc.
// Characters written directly to an xmlStream via print_cr, etc.,
// are directly written to the encapsulated stream, xmlStream::out().
// This can be used to produce markup directly, character by character.
// (Such writes are not checked for markup syntax errors.)

class xmlStream : public outputStream {
  friend class defaultStream; // tty
 public:
  enum MarkupState { BODY,       // after end_head() call, in text
                     HEAD,       // after begin_head() call, in attrs
                     ELEM };     // after begin_elem() call, in attrs

 protected:
  outputStream* _out;            // file stream by which it goes
  julong        _last_flush;     // last position of flush
  MarkupState   _markup_state;   // where in the elem/head/tail dance
  outputStream* _text;           // text stream
  xmlTextStream _text_init;

  // for subclasses
  xmlStream() {}
  void initialize(outputStream* out);

  // protect this from public use:
  outputStream* out()                            { return _out; }

  // helpers for writing XML elements
  void          va_tag(bool push, const char* format, va_list ap);
  virtual void see_tag(const char* tag, bool push) NOT_DEBUG({});
  virtual void pop_tag(const char* tag) NOT_DEBUG({});

#ifdef ASSERT
  // in debug mode, we verify matching of opening and closing tags
  int   _element_depth;              // number of unfinished elements
  char* _element_close_stack_high;   // upper limit of down-growing stack
  char* _element_close_stack_low;    // upper limit of down-growing stack
  char* _element_close_stack_ptr;    // pointer of down-growing stack
#endif

 public:
  // creation
  xmlStream(outputStream* out) { initialize(out); }
  DEBUG_ONLY(virtual ~xmlStream();)

  bool is_open() { return _out != NULL; }

  // text output
  bool inside_attrs() { return _markup_state != BODY; }

  // flushing
  virtual void flush();  // flushes out, sets _last_flush = count()
  virtual void write(const char* s, size_t len);
  void    write_text(const char* s, size_t len);  // used by xmlTextStream
  int unflushed_count() { return (int)(out()->count() - _last_flush); }

  // writing complete XML elements
  void          elem(const char* format, ...);
  void    begin_elem(const char* format, ...);
  void      end_elem(const char* format, ...);
  void      end_elem();
  void          head(const char* format, ...);
  void    begin_head(const char* format, ...);
  void      end_head(const char* format, ...);
  void      end_head();
  void          done(const char* format, ...);  // xxx_done event, plus tail
  void          done_raw(const char * kind);
  void          tail(const char* kind);

  // va_list versions
  void       va_elem(const char* format, va_list ap);
  void va_begin_elem(const char* format, va_list ap);
  void       va_head(const char* format, va_list ap);
  void va_begin_head(const char* format, va_list ap);
  void       va_done(const char* format, va_list ap);

  // write text (with quoting of special XML characters <>&'" etc.)
  outputStream* text() { return _text; }
  void          text(const char* format, ...);
  void       va_text(const char* format, va_list ap) {
    text()->vprint(format, ap);
  }

  // commonly used XML attributes
  void	        stamp();                 // stamp='1.234'
  void	        method(methodHandle m);  // method='k n s' ...
  void	        klass(KlassHandle k);    // klass='name'
  void	        name(symbolHandle s);    // name='name'

  /*  Example uses:

      // Empty element, simple case.
      elem("X Y='Z'");          <X Y='Z'/> \n

      // Empty element, general case.
      begin_elem("X Y='Z'");    <X Y='Z'
      ...attrs...               ...attrs...
      end_elem();               />

      // Compound element, simple case.
      head("X Y='Z'");          <X Y='Z'> \n
      ...body...                ...body...
      tail("X");                </X> \n

      // Compound element, general case.
      begin_head("X Y='Z'");    <X Y='Z'
      ...attrs...               ...attrs...
      end_head();               > \n
      ...body...                ...body...
      tail("X");                </X> \n

      // Printf-style formatting:
      elem("X Y='%s'", "Z");    <X Y='Z'/> \n

   */

};

// Standard log file, null if no logging is happening.
extern xmlStream* xtty;

// Note:  If ::xtty != NULL, ::tty == ::xtty->text().
