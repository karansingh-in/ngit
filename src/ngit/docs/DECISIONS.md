## Why SHA-256 instead of SHA-1?

Git uses SHA-1.

NGit uses SHA-256 because:
- stronger hash

## Why plain Java?

Not using Maven initially because I wanted to
understand every dependency and keep the project simple.

## Why HashMaps for index?

To avoid redundancy of files, if a file is added multiple times, we only take the latest one for commit.

## How am i restoring contents of a branch?

Calling the status command to check if all the files 
are committed or not. Then if everything is fine,
we delete everything in the tracked list and restore 
the files from the tree's snapshot.

[ Currently not checking for deduplicates, leaving that for future scope. ]