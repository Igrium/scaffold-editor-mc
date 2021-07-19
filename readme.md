# Scaffold Editor

Scaffold is a Minecraft mapmaking toolset based on similar concepts to Source engine. It is designed to create a non-destructive mapmaking workflow that makes it easier to manage your project by breaking it up into smaller files that can be individually worked with. It also improves compatibility with [Version Control](https://git-scm.com/book/en/v2/Getting-Started-About-Version-Control).

*NOTE: Because Scaffold is still in development, anything on this repo is subject to change or may not be functional yet. Do not use in a production envoirnment. (If you do use it in a production envoirnment, let me know how it goes!)*

## How it works
Unlike traditional Minecraft level editors, Scaffold does not directly edit Minecraft save files. Instead, it uses a proprietary xml-based format called `mclevel`, which contains instructions for building the level at compile-time.

Each level contains a list of "entities" (not to be confused with Minecraft entities) that all run unique code upon compilation. Entities can be anything from a basic set of blocks to a fully functioning npc. If you can make it with a datapack, it can be an entity. You can also import other level files as entities for further modularity (this hasn't been implemented yet). This allows for a non-destructive workflow that can easily contain custom functionality and is compatable with Version Control.

Level files are stored inside a project folder which contains everything related to the project. Inside this project folder, along with the `maps` folder for the levels, there is an `assets` folder which contains assets that will go in the resourcepack, a `data` folder which contains functions and other data that will go in the datapack, a `scripts` folder which contains Python scripts, and a variety of other folders with more esoteric uses.

Scaffold encourages a workflow that's *vastly* different to the traditional mapmaking workflow; make sure to allocate time to get used to it. An example map can be found [here.](https://github.com/Sam54123/scaffold_parkour)

## Internals

The core code that makes Scaffold work is available in a [seperate repo.](https://github.com/Sam54123/Scaffold) Details on how the whole thing works are presented there.