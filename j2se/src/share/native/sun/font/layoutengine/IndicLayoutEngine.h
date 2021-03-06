
/*
 * @(#)IndicLayoutEngine.h	1.7 03/10/24 
 *
 * (C) Copyright IBM Corp. 1998-2003 - All Rights Reserved
 *
 */

#ifndef __INDICLAYOUTENGINE_H
#define __INDICLAYOUTENGINE_H

#include "LETypes.h"
#include "LEFontInstance.h"
#include "LEGlyphFilter.h"
#include "LayoutEngine.h"
#include "OpenTypeLayoutEngine.h"

#include "GlyphSubstitutionTables.h"
#include "GlyphDefinitionTables.h"
#include "GlyphPositioningTables.h"

#include <string.h>

class MPreFixups;

/**
 * This class implements OpenType layout for Indic OpenType fonts, as
 * specified by Microsoft in "Creating and Supporting OpenType Fonts for
 * Indic Scripts" (http://www.microsoft.com/typography/otspec/indicot/default.htm)
 *
 * This class overrides the characterProcessing method to do Indic character processing
 * and reordering, and the glyphProcessing method to implement post-GSUB processing for
 * left matras. (See the MS spec. for more details)
 *
 * @internal
 */
class IndicOpenTypeLayoutEngine : public OpenTypeLayoutEngine
{
public:
    /**
     * This is the main constructor. It constructs an instance of IndicOpenTypeLayoutEngine for
     * a particular font, script and language. It takes the GSUB table as a parameter since
     * LayoutEngine::layoutEngineFactory has to read the GSUB table to know that it has an
     * Indic OpenType font.
     *
     * @param fontInstance - the font
     * @param scriptCode - the script
     * @param langaugeCode - the language
     * @param gsubTable - the GSUB table
     *
     * @see LayoutEngine::layoutEngineFactory
     * @see OpenTypeLayoutEngine
     * @see ScriptAndLangaugeTags.h for script and language codes
     *
     * @internal
     */
    IndicOpenTypeLayoutEngine(const LEFontInstance *fontInstance, le_int32 scriptCode, le_int32 languageCode,
                            const GlyphSubstitutionTableHeader *gsubTable);

    /**
     * This constructor is used when the font requires a "canned" GSUB table which can't be known
     * until after this constructor has been invoked.
     *
     * @param fontInstance - the font
     * @param scriptCode - the script
     * @param langaugeCode - the language
     *
     * @see OpenTypeLayoutEngine
     * @see ScriptAndLangaugeTags.h for script and language codes
     *
     * @internal
     */
    IndicOpenTypeLayoutEngine(const LEFontInstance *fontInstance, le_int32 scriptCode, le_int32 languageCode);

    /**
     * The destructor, virtual for correct polymorphic invocation.
     *
     * @internal
     */
   virtual ~IndicOpenTypeLayoutEngine();

protected:

    /**
     * This method does Indic OpenType character processing. It assigns the OpenType feature
     * tags to the characters, and may generate output characters which have been reordered. For
     * some Indic scripts, it may also split some vowels, resulting in more output characters
     * than input characters.
     *
     * Input parameters:
     * @param chars - the input character context
     * @param offset - the index of the first character to process
     * @param count - the number of characters to process
     * @param max - the number of characters in the input context
     * @param rightToLeft - true if the characters are in a right to left directional run
     *
     * Output parameters:
     * @param outChars - the output character arrayt
     * @param charIndices - the output character index array
     * @param featureTags - the output feature tag array
     * @param success - set to an error code if the operation fails
     *
     * @return the output character count
     *
     * @internal
     */
    virtual le_int32 characterProcessing(const LEUnicode chars[], le_int32 offset, le_int32 count, le_int32 max, le_bool rightToLeft,
            LEUnicode *&outChars, le_int32 *&charIndices, const LETag **&featureTags, LEErrorCode &success);

    /**
     * This method does character to glyph mapping, applies the GSUB table and applies
     * any post GSUB fixups for left matras. It calls OpenTypeLayoutEngine::glyphProcessing
     * to do the character to glyph mapping, and apply the GSUB table.
     *
     * Note that in the case of "canned" GSUB tables, the output glyph indices may be
     * "fake" glyph indices that need to be converted to "real" glyph indices by the
     * glyphPostProcessing method.
     *
     * Input parameters:
     * @param chars - the input character context
     * @param offset - the index of the first character to process
     * @param count - the number of characters to process
     * @param max - the number of characters in the input context
     * @param rightToLeft - true if the characters are in a right to left directional run
     * @param featureTags - the feature tag array
     *
     * Output parameters:
     * @param glyphs - the output glyph index array
     * @param charIndices - the output character index array
     * @param success - set to an error code if the operation fails
     *
     * @return the number of glyphs in the output glyph index array
     *
     * Note: if the character index array was already set by the characterProcessing
     * method, this method won't change it.
     *
     * @internal
     */
    virtual le_int32 glyphProcessing(const LEUnicode chars[], 
        le_int32 offset, le_int32 count, le_int32 max, le_bool rightToLeft,
        const LETag **&featureTags, LEGlyphID *&glyphs, le_int32 *&charIndices, 
        LEErrorCode &success);

private:
    MPreFixups *fMPreFixups;
};

#endif

