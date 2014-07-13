mproncace's ten.java submission
==============================

[![ten.java](https://cdn.mediacru.sh/hu4CJqRD7AiB.svg)](https://tenjava.com/)

This is a submission for the 2014 ten.java contest.

- __Theme:__ Random events
- __Time:__ Time 3 (7/12/2014 14:00 to 7/13/2014 00:00 UTC)
- __MC Version:__ 1.7.10 (latest Bukkit beta)
- __Stream URL:__ https://twitch.tv/shadypotat0

<!-- put chosen theme above -->

---------------------------------------

Usage
-----

__Corruption__ is a plugin which requires only that you install and configure it, and it'll do it's thing once running. Each second, it has a (configurable) chance to corrupt a random surface block in one of the loaded chunks. Once a block is corrupted, it turns to netherrack (or another configurable block) and has the potential each second to spread its corruption to other blocks. Corruption cannot be reversed, though it can be halted with a block of glowstone (or another configurable block). The preventative block will protect blocks within a configurable radius (default 5 meters) from corruption.

Additionally, you may configure which block types may be selected for corruption, and which block types corruption may spread to. This is done via a string list in the config.yml file.

For testing purposes, I recommend you set the corruption chance to ~20, and the spread chance to ~40-60. Be warned that lower values may cause decreased server responsiveness and increased memory usage.
