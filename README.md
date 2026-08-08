# ngit

A simple Git-like version control system written from scratch in Java.

## About

`ngit` is a learning project that reimplements core version-control concepts without relying on Git internally.

The goal is to understand how systems like Git work under the hood, including:

* Content-addressed object storage
* File indexing
* Commits
* Branches
* Repository management
* Eventually remote operations

## Current Progress

* [x] Repository structure
* [x] SHA-256 object hashing
* [x] Object storage and compression
* [x] Repository path management
* [x] Index
* [x] `init`
* [x] `add`
* [ ] `status`
* [ ] `commit`
* [ ] `log`
* [ ] Branches
* [ ] Checkout
* [ ] Remote repositories
* [ ] Push / Pull

## Tech Stack

* Java
* Java NIO
* SHA-256
* Git-inspired content-addressed storage

## Status

Currently under active development.
