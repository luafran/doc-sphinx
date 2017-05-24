Sphinx Tutorial
***************

Section 1
=========

Paragraphs
----------

Chapters, sections and sub-sections are defined with the character used to underline titles.

Normally, there are no heading levels assigned to certain characters as the
structure is determined from the succession of headings.  However, this
convention is used in `Python's Style Guide for documentating
<https://docs.python.org/devguide/documenting.html#style-guide>`_ which you may
follow:

* ``#`` with overline, for parts
* ``*`` with overline, for chapters
* ``=``, for sections
* ``-``, for subsections
* ``^``, for subsubsections
* ``"``, for paragraphs

The paragraph is the most basic block in a reST document. Paragraphs are simply chunks of text
separated by one or more blank lines. As in Python, indentation is significant in reST, so all
lines of the same paragraph must be left-aligned to the same level of indentation.

And yet another paragraph.

Lists and Quote-like blocks
---------------------------

Bullet lists are created with asterisks. Like this: 

* This is a bulleted list.
* It has two items, the second
  item uses two lines. If you continue on next line make sure to start at the same column.

Numbered lists can be explicitly numbered or with autonumbering using # sign.

1. This is a numbered list.
2. It has two items too.

#. This is an autonumbered list.
#. It has two items too.

Nested lists are possible, but be aware that they must be separated from the parent list items by blank lines:

* this is
* a list

  * with a nested list
  * and some subitems

* and here the parent list continues

Line blocks are a way of preserving line breaks:

| These lines are
| broken exactly like in
| the source file.
|

We need to add another line to separate the nex paragraph?

Text Emphasis
-------------

* one asterisk for *emphasis* (italics),
* two asterisks for **strong emphasis** (boldface)
* backquotes for ``code samples``.

.. Warning::

   Text

.. seealso::

   Text

.. note::

   Text

Tables
------

Grid tables provide a complete table representation via grid-like "ASCII art". Grid tables allow arbitrary cell contents (body elements),
and both row and column spans. However, grid tables can be cumbersome to produce.
This is a grid table. 

+------------------------+------------+----------+----------+
| Header row, column 1   | Header 2   | Header 3 | Header 4 |
| (header rows optional) |            |          |          |
+========================+============+==========+==========+
| body row 1, column 1   | column 2   | column 3 | column 4 |
+------------------------+------------+----------+----------+
| body row 2             | Cells may span columns.          |
+------------------------+------------+---------------------+
| body row 3             | Cells may  | - Table cells       |
+------------------------+ span rows. | - contain           |
| body row 4             |            | - body elements.    |
+------------------------+------------+---------------------+


And this is a csv-table

.. csv-table:: Table title
    :header: "first header", "second header", "third header"
    :widths: 20, 20, 10

    "item 1", "item 2", 3
    "item 4", "item 5", 5

Footnotes
---------

For footnotes use ``[#name]_`` to mark the footnote
location, and add the footnote body at the bottom of the document after a
"Footnotes" rubric heading, like so::

    Lorem ipsum [#f1]_ dolor sit amet ... [#f2]_

    .. rubric:: Footnotes

    .. [#f1] Text of the first footnote.
    .. [#f2] Text of the second footnote.

You can also explicitly number the footnotes (``[1]_``) or use auto-numbered
footnotes without names (``[#]_``).

Let's use footnotes here [#f1]_ and here [#f2]_

Citations
---------

Standard reST citations are supported, with the
additional feature that they are "global", i.e. all citations can be referenced
from all files.  Use them like so::

    Lorem ipsum [Ref]_ dolor sit amet.

    .. [Ref] Book or article reference, URL or whatever.

Citation usage is similar to footnote usage, but with a label that is not
numeric or begins with ``#``.

Lets use a citation here [Ref]_

.. [Ref] Book or article reference, URL or whatever.

Substitutions
-------------

reST supports "substitutions", which are pieces of text and/or markup referred
to in the text by ``|name|``.  They are defined like footnotes with explicit
markup blocks, like this::

    .. |name| replace:: replacement *text*

or this::

    .. |caution| image:: warning.png
                 :alt: Warning!

Comments
--------

Every explicit markup block which isn’t a valid markup construct is regarded as a comment.
There is a comment here like this::

    .. This is a comment.

.. This is a comment

Images and figures
------------------

Examples of SVG/EPS figure. Figure filename extension should be * so Sphinx choose the
right format according to the output.

.. figure:: ./images/fig1.*
    :alt: figure 1
    :align: center

    This is the caption for figure 1

Links and Embedded HTML
-----------------------

Adding a simple link. Will open in same page.

`Databus Javadoc <http://databus-doc.fastdxl.net/3.1/sdk-javadoc/index.html>`_


This is a link that will open a new tab. We used raw html and a substitution.

.. |timetable| raw:: html

   <a href='http://databus-doc.fastdxl.net/3.1/sdk-javadoc/index.html' target='_blank'>javadoc</a>


|timetable|

Another example of embedded HTML that creates an iframe

.. raw:: html

    <div style="margin-top:10px;">
        <iframe width="745" height="700" src="http://databus-doc.fastdxl.net/3.1/sdk-javadoc/index.html" allowfullscreen></iframe>
    </div>

Some text

Include Source Code
-------------------

Some Java example from file. Note lines with emphasis

.. literalinclude:: examples/ProducerExample.java
    :language: java
    :emphasize-lines: 3-4,10-11 
    :linenos:


.. Keep Footnotes at the end of the document
.. rubric:: Footnotes

.. [#f1] Text of the first footnote.
.. [#f2] Text of the second footnote.
