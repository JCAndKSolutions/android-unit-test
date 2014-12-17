#!/bin/bash
###
### The following block runs after commit to "master" branch
###
if [ `git rev-parse --abbrev-ref HEAD` == "master" ]; then

    # Layout prefix is prepended to each markdown file synced
    ###################################################################
    LAYOUT_PREFIX='---\r\nlayout: index\r\n---\r\n\r\n'

    # Switch to gh-pages branch to sync it with master
    ###################################################################
    git checkout gh-pages

    # Sync the README.md in master to index.md adding jekyll header
    ###################################################################
    git checkout master -- README.md
    echo -e $LAYOUT_PREFIX > index.md
    cat README.md >> index.md
    rm README.md
    git add index.md
    git commit -a -m "Sync README.md in master branch to index.md in gh-pages"

    # Sync the markdown files in the docs/* directory
    ###################################################################
    #git checkout master -- docs
    #FILES=docs/*
    #for file in $FILES
    #do
    #    echo -e $LAYOUT_PREFIX | cat - "$file" > temp && mv temp "$file"
    #done

    #git add docs
    #git commit -a -m "Sync docs from master branch to docs gh-pages directory"

    # Uncomment the following push if you want to auto push to
    # the gh-pages branch whenever you commit to master locally.
    # This is a little extreme. Use with care!
    ###################################################################
    # git push origin gh-pages

    # Finally, switch back to the master branch and exit block
    git checkout master
fi