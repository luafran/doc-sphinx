## Example documentation project using Sphinx

### Introduction

This is the DXL Streaming Documentation Project.

It uses Sphinx + javasphinx to generate documentation.
It uses AsciiToSVG to generate diagrams form ascii figures.
It uses sphinxcontrib.httpdomain to generate REST APIs documentation.

Created the project with sphinx-quickstart
Then modified Makefile to build svg and png images from txt images using AsciiToSVG

### Environment Setup

In Debian to build HTML and PDF install

```
sudo apt-get install inkscape texlive-latex-extra
(Check if other textlive is missing)
```

Create virtualenv and install requirements.txt

```
virtualenv venv
source venv/bin/activate
pip install -r requirements.txt
```

### How to Build
```
make html
```

Output is generated in build/html

### Project Structure

All documentation sources are located in source directory. One file per chapter.
Project entry point is source/index.rst. This file should contain just the TOC.
Each chapter should be located in a separated file.

See source/sphinx-tutorial.rst for examples about how to do different things using Sphinx


### References

[Sphinx](http://www.sphinx-doc.org/)

[javasphinx](https://bronto.github.io/javasphinx/)

[Sphinx Usage Examples](http://www.astro.rug.nl/~vogelaar/sphinx/build/html/index.html)

[AsciiToSVG](https://github.com/dhobsd/asciitosvg)

[sphinxcontrib.httpdomain](https://pythonhosted.org/sphinxcontrib-httpdomain/)
