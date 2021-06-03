# Mathematical LaTeX Helper

![Screenshot](https://moritzf.de/user/pages/02.projects/03.mathematical-latex-helper/mathematicallatexhelper.png)

[[Download on GitHub](https://github.com/moritzfl/mathematicallatexhelper/releases)]

**Note: This software requires a [java-environment](https://www.oracle.com/technetwork/java/javase/downloads/index.html)**

Mathematical LaTeX Helper is a tool for rendering  mathematical LaTeX-expressions to images. It does not require a LaTeX installation on your system.

The tool provides the user with a very simplistic gui that renders latex-expressions directly while typing and is able to copy the rendered images to the clipboard or alternatively save them to the desktop as pdf and png. You should always try to use pdf if it fits your use case. This is because the text in pdfs is searchable and the fonts are vectorized. A png uses pixels instead and therefore is limited in resolution and is not searchable.
Microsoft Office 2019 supports the insertion of pdfs as images.

Mathematical LaTeX Helper is able to extract the original latex-expression out of the rendered images or pdfs by dropping them to the text input area in the user interface. In order to get the best results, try to use the rendered files directly as input. As the latex-expression is encoded in that file (both png and pdf), the restoration of the expression is always precise in this case.

Microsoft Office 2019 changes some aspects of the image. In order to retrieve the exact expression that was originally used, it is best to use the pdf files from Mathematical Latex Helper when working with Office. From your document, you can rightclick the image and save it to your computer as a pdf. That pdf still contains embedded information that allows a precise extraction of the original expression.

However, when using png-files in Office, this is not the case. In that situation, Mathematical LaTeX Helper will try its best to apply [OCR](https://en.wikipedia.org/wiki/Optical_character_recognition) to the image to extract the LaTeX-expression. 
Technically this means that you could also feed it with formulas that were not rendered by Mathematical LaTeX Helper. However, the OCR libary used is a bit picky and therefore the quality of results might vary. For the best possible results, you can also configure access to the MathPix webservice that is more capable than the OCR library integrated in Mathematical LaTeX Helper.

Mathematical LaTeX Helper is licensed under the terms of GPL3.

Mathematical LaTeX Helper makes use of the following work by other authors (and further depends on their transitive dependencies):

- [JLaTeXMath](https://github.com/opencollab/jlatexmath)
- [iTextPdf](https://github.com/itext/itextpdf)
- [PdfBox](https://pdfbox.apache.org)
- [MathOCR](https://github.com/chungkwong/MathOCR)
- [MathPix API](https://docs.mathpix.com)
- [Gutenberg](https://github.com/Arnauld/gutenberg)
- [Steganography Tutorial](https://www.dreamincode.net/forums/topic/27950-steganography/)
- [Directories-JVM](https://github.com/soc/directories-jvm)
