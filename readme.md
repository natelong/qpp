# QPP - The quick, simple, generic preprocessor
I wanted a preprocessor that I could use with Javascript, but I couldn't find a good one that struck the right balance between simple and powerful, so I made my own.

## What does it do?
In a nutshell, QPP allows you to use "#include" directives in any text file to add in the content from external files. Right now it's pretty barebones, but I'm hoping to add more features in the near future.

## How do I use it?

### #include "filename"
This statement allows you to add the contents from one file into another file. Just specify the filename in quotes, and QPP will go fetch that content and plop it into your file. You can even use URLs!

Examples:  
`#include "http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"`  
`#include "classes/MyClass.js"`

### Caching
QPP will cache remote files for faster processing, so you can clear the cache by deleting .qpp-remote-cache. At some point, you'll be able to pass in a commend-line flag to bypass the cache, but for now, you'll have to delete the folder manually.

## This doesn't do much. What's the deal?

It's not done yet :) Here's a to-do list:

1.  Add a flag to bypass the cache
2.  Build out a better command-line interface (help text, better error messaging, etc.)
3.  Add #IFDEF statements for conditional compilation.
4.  ???
5.  Profit.