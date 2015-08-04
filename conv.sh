
# display: find . -name "*.java" -exec echo "mv  {} {}.old"  \; -exec echo  "iconv -f WINDOWS-1252 -t UTF-8 {}.old -o {}"  \; -exec echo "rm -f {}.old"  \;


#execute: find . -name "*.java" -exec mv  {} {}.old  \; -exec iconv -f WINDOWS-1252 -t UTF-8 {}.old -o {}  \; -exec rm -f {}.old  \;
