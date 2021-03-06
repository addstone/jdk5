/*
 * @(#)HanLayoutEngine.h	1.2 03/08/01
 *
 * HanLayoutEngine.h: OpenType processing for Han fonts.
 *
 * (C) Copyright IBM Corp. 1998-2003 - All Rights Reserved.
 *
 */

#ifndef __HANLAYOUTENGINE_H
#define __HANLAYOUTENGINE_H

#include "LETypes.h"
#include "LEFontInstance.h"
#include "LayoutEngine.h"
#include "OpenTypeLayoutEngine.h"

#include "GlyphSubstitutionTables.h"

/**
 * This class implements OpenType layout for Han fonts. It overrides
 * the characerProcessing method to assign the correct OpenType feature
 * tags for the CJK language-specific forms.
 *
 * @internal
 */
class HanOpenTypeLayoutEngine : public OpenTypeLayoutEngine
{
public:
    /**
     * This is the main constructor. It constructs an instance of HanOpenTypeLayoutEngine for
     * a particular font, script and language. It takes the GSUB table as a parameter since
     * LayoutEngine::layoutEngineFactory has to read the GSUB table to know that it has a
     * Han OpenType font.
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
    HanOpenTypeLayoutEngine(const LEFontInstance *fontInstance, le_int32 scriptCode, le_int32 languageCode,
                            const GlyphSubstitutionTableHeader *gsubTable);


    /**
     * The destructor, virtual for correct polymorphic invocation.
     *
     * @internal
     */
    virtual ~HanOpenTypeLayoutEngine();

protected:

    /**
     * This method does Han OpenType character processing. It assigns the OpenType feature
     * tags to the characters to generate the correct language-specific variants.
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
    virtual le_int32 characterProcessing(const LEUnicode chars[], le_int32 offset, le_int32 count, le_int32 max, 
      le_bool rightToLeft, LEUnicode *&outChars, le_int32 *&charIndices, const LETag **&featureTags, LEErrorCode &success);
};

#endif

