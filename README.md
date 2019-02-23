# Mathematical LaTeX Helper

[Mathematical LaTeX Helper](https://github.com/moritzfl/mathematicallatexhelper) is a tool for rendering  mathematical LaTeX-expressions to images. It does not rely on any other software being installed on your system except for Java 8 or newer (Java 9 or above is recommended). It works on all major desktop operating systems (Windows, macOS, Linux).

![Screenshot](http://www.moritzf.de/projects/media/mathematicallatexhelper.png)

The tool provides the user with a very simplistic gui that renders latex-expressions directly while typing and is able to copy the rendered images to the clipboard or alternatively save them to the desktop as pdf and png. Always try to use pdf if your software supports it as this results in vector based and searcheable fonts while png is a raster graphic. An example for software supporting pdfs as images is Microsoft Office 2019 for Mac (not sure about Windows but that might work as well).

Mathematical LaTeX Helper is able to extract the original latex-expression out of the rendered images or pdfs by dropping them to the editor in the user interface. In order to get the best results, try to use the rendered files directly as input. As the latex-expression is encoded in that file, the restoration of the expression is always precise in this case.

However, some software may change the data when the picture is used. For example Microsoft Office will remove the necessary information when using a image within the software. In this case, you can first rightclick the image in Microsoft Office and save the picture to a folder. Then drag the picture to the editor-area of Mathematical LaTeX Helper. Mathematical LaTeX Helper will try its best to apply [OCR](https://en.wikipedia.org/wiki/Optical_character_recognition) to the image to extract the LaTeX-expression. The extracted expression might not always be perfect in this case.
Technically this means that you could also feed it with formulas that were not rendered by Mathematical LaTeX Helper. However, the OCR libary used is a bit picky and therefore the quality of results might vary.

Mathematical LaTeX Helper is licensed under the terms of GPL3.

Mathematical LaTeX Helper makes use of the following work by other authors (and further depends on their transitive dependencies):

- [JLaTeXMath](https://github.com/opencollab/jlatexmath)
- [iTextPdf](https://github.com/itext/itextpdf)
- [PdfBox](https://pdfbox.apache.org)
- [MathOCR](https://github.com/chungkwong/MathOCR)
- [MathPix API](https://docs.mathpix.com)
- [Gutenberg](https://github.com/Arnauld/gutenberg)
- [Steganography Tutorial](https://www.dreamincode.net/forums/topic/27950-steganography/)
