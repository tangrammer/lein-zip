# tangrammer/lein-zip

on top of original lein-zip, this version has the notion of environments
so you can package file-prod.ext as file.ext if you need too

This feature was relevant when i need to work with different envs: dev/test/prod

## Example Usage

```clojure
[tangrammer/lein-zip "0.1.3"]
```

Add the following to project.clj

```clojure
:zip-env-files ["file1"]
:zip ["file1" "resources/file2" "target/production.jar" "folder1"]
```

Then

    $ lein zip-env
    or
    $ lein zip-env prod

This will produce target/project-version.zip
