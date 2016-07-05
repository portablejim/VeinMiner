Changelog
=========

0.33.2
------
Fixes:
* Fix possible issues with tinkers construct tools.

0.33.0
------
Changes:
* API: InitialToolCheck includes Point parameter.
* API: HarvestFailedCheck includes Point parameter.
* API: PostUseTool includes Point parameter.
Fixes:
* InitialToolCheck is actually fired

0.32.0
------
Changes:
* Rewrite some things. Hopefully things still work.
* If you have the mod client-side (and server side) you can no longer change mode using /veinminer mode <mode>.
  Now you need to use /veinminerc <mode>
* There will be a few more information about the state of Veinminer posted to chat log.
* Tool auto-detect config option is now in "modSupport.cfg"
* Add blacklist for tinkers tools
Fixes:
* Tool auto-detect actually works.

0.31.6
------
Changes:
* Allow items from excompressum work with VeinMiner.

0.31.4
------
Fixes:
* Allow IC2 rubber wood to work with VeinMiner.

0.31.3
------
Fixes:
* Further attempts to fix ConcurrentModificationException crashes.

0.31.1
------
Fixes:
* Fix possible crashes with ConcurrentModificationException.

0.31.0
------
Fixes:
* Torches and grass on top of blocks destroyed by veinminer now drop properly.
* Veinminer should be faster, working closer to the advertised speed.

0.30.2
------
Fixes:
* The command to reload configs from file is 'reloadconfig' not 'loadconfig'
* The per tick command returns the correct value.

0.30.1
------
Bob Ross edition.
Changes/Fixes:
* You can now have happy little accidents with happy little white trees. Instead of a 2 inch brush, use a crook.

0.30.0
-------
Changes:
* Add a '/veinminer reloadconfig' command. This reloads the configs from the config file (yes, it does what it says on the tin).
* New config option 'expmodifier' to customise the amount of experience is used (and is required) for each block VeinMiner mines.
* New config option 'hungermodifier' to customise how much extra hunger each block takes to mine.

0.29.1
------
Changes:
* Change the way that veinminer works. You should not see a difference in the way it works.
Fixes:
* Fix crash when veinmining using a AOE tool (tinkers hammer).
* Fix crash with a tooltip.

0.28.3
------
Changes:
* Any tool that extends Ex Nihilo's ItemHammerBase class will work with VeinMiner.

0.28.2
------
Fixes:
* Don't crash when blocks drop entities (yes, I'm looking at you, Ag Skies hellfish)

0.28.1
------
Fixes:
* Stop console spam.

0.28.0
------
Changes:
* No longer a coremod.
Fixes:
* Fix some minor errors causing crashes.

0.27.1
------
Fixes:
* More null checking

0.27.0
------
Changes:
* Re-implement networking code. Should cause less problems.

0.26.1
------
Fixes:
* Make blocklimit and range commands work correctly (or at least, work better).

0.26.0
------
Changes:
* Add french translations.
Fixes:
* Fix bug in version checker.
* Fix title

0.25.1
------
Fails to implement version checker correctly.
Known issues: Can't connect to servers.

0.25.0
------
Changes:
* Now have Veinminer API as api annotation.
Fixes:
* Fail at fixing connecting to a server with Veinminer without having it on the client.
Known issues: Can't connect to servers.

0.23.0
------
Fixes:
* Fix checking of the blocklimit when mining.

0.22.0
------
Changes:
* Veinminer can now be configured from the server console.

0.21.2
------
Changes:
* Now crashes minecraft on invalid config.
Fixes:
* Update '/veinminer mode' help.

0.21.1
------
Fixes:
* Several null pointer exceptions

0.21.0
------
Changes:
* Stop VeinMiner Mod Support adding tools after first launch (via detecting if its config file is present)
* Increase number of characters allowed in the block/tool configuration GUI.

0.20.0
------
Changes:
* API: Add ability to set equal blocks.
* Redo configuration GUI to allow customisation of all defined tool types.

0.19.0
------
Changes:
* Re-enable IMC support (forgot to tell you I disabled it).
* API
   + Now uses string tool names
   + Now allows you to add tool types
* [Mod support] Add Ex Nihilo crooks and hammers by default if installed.

0.18.1
------
Changes:
 * Make tool list and block list for tools defined in a JSON-based config file.
     This allows custom tools to be defined.
 * Extend command system to allow you to add tools and blocks to custom tools
     defined in the config file.
 * Move config files to a new 'veinminer' folder
Fixes:
* Make setting range too high result in the range set to max, instead of
    setting it to something less.
* Don't change block limit to radius when setting radius via gui.

0.17.1
------
Fixes:
* Stop Veinminer from sticking on.
* Fix client Preferred mode comment say "no_sneak" instead of "nosneak".

0.17.0
------
Changes:
* When ids are shown using F3 + H, show the internal name of the item/block.

Fixes:
* Fix /help
* Fix crash when viewing config gui.


0.16.1
------
Fixes:
* Fix 1.7.10 errors
* Make packets less leaky

0.16.0
------
Changes:
* Updated to Minecraft 1.7.10 Prerelease-4
* Reduced message spam.

0.15.0
------
Changes:
* Added back shift/no_shift mode.
Fixes:
* Spelling
* Ex Nihilo hammers should now work.

0.14.1
------
Fixes:
* Tools now work when in multiple categories
* No longer crash when mining in some circumstances.

0.14.0
------
Changes:
* Update and include VeinMiner Mod Support, configured to auto-add tools from a few popular mods.
* Config filename now start with uppercase.
Fixes:
* Redstone ore will now work properly.

0.13.0
------
Changes:
* Upgrade to Minecraft 1.7.2
* Added GUI to configure Veinminer
* Simplified keybind options to take advantage of new keybinding code.
* Changed method of detecting drops so all drops (e.g. from clay) are picked up and collated.

0.12.2
------
Fixes:
* Fix config file up.

0.12.1
------
Fixes:
* Stop spamming of "You are too hungry..." when not using Veinminer.

0.12.0
------
Changes:
* Rename stuff in API. Breaks almost anything using API, including VeinMiner Mod support. (hint: there is an update to fix that)
* Add more tools: Shears and Hoe.

0.11.0
------
Changes:
* More API changes, adding extra stuff. Should not break anything.

0.10.0
------
Changes:
* API changed to be more flexible. This breaks almost everything using the API, including VeinMiner Mod Support.

Yes people, this is what comes after version x.9, version x.10.

0.9.4
-----
Fixes:
* Fix crash when starting up with some mods (FormatException).

0.9.3
-----
Fixes:
* Fix crash with autodection for some mod's blocks

0.9.2
-----
Fixes:
* Fix startup crash on dedicated servers.

0.9.1
-----
Fixes:
* Fix non-equivalent blocks being detected as equivalent.

0.9
---

Changes:
* Improve detection of equal blocks.
* Added autoaddition of blocks matching oredict (+ config settings).

Fixes:
* A few config-related bugs.

0.8.3
------
Fixes:
* Fix null pointer that is possibly encountered in creative mode.
* Fix continuing to mine if tool runs out.

0.8.2
------
Fix:
* Actually fix mining of 'insta-break' blocks.

0.8.1
------
Change:
* Increase hunger usage
* Fix bug with mining 'insta-break' blocks. In 0.8.0 it only worked in creative mode.

0.8.0
------
* Re-write API. NOTE: YOU NEED A NEW VERSION OF VEINMINER MOD SUPPORT.
* Improve format in config file. NOTE: YOU NEED TO DELETE YOUR CONFIG FILE SO A NEW ONE GENERATES.
* Allow VeinMiner to mine 'insta-break' blocks, such as long grass.
* Fix bug with being able to use VeinMiner with blocks you cannot harvest.
* Change the font back to the default font.

0.7.3
-----
* Fix logic problem with API.

0.7.2
-----
Change:
* Improve support for tools.

Fixes:
* Fix API to work properly. (hopefully)

0.7.1
-----
Fix:
* Fix (another) crash on mining with no tool.

0.7.0
-----
Changes:
* Change config defaults to make them closer to gameplay values
* Increase hunger used when mining.
* Change command `/veinminer enable` to `/veinminer mode`
* Add commands to change config file settings.
* Add command to save settings back to config file.
* Re-do API to use Forge event system.
* Allow versions with same major and minor version (first 2 numbers) to connect.

Fixes:
* Fix negative values in config file actually working.
* Fix messages to clients without VeinMiner.
* Don't auto-grab drops with NBT data. Stops Veinminer messing with them.

0.6.5
------
* Fix crash when not using a tool.

0.6.4
-----
* Actually fix crash introduced by 0.6.2

0.6.3
------
Fixes:
* Fix crash introduced by 0.6.2

0.6.2
------
Fixes:
* Fix bug introduced 0.6.1 that stopped veinminer working

0.6.1
-----
Fixes:
* Fix no_sneak command

0.6.0
-----
Changes
* Move user visible strings to lang files (will provide easier localization)
* Move mcmod.info into code
* Make coremod that has been in there all along visible via a child mod
* Fix file to refer to 1.5 instead of 1.5.2
* Fix any problems with older builds of Minecraft (1.5.0 and 1.5.1)
* Silence the debug statements from VeinMiner littering log files
* Implement mod signing. Provides an easy way to see if the file has been tampered with. It only shouts at you in the log files as a warning

0.5.1
-----
Changes
* Actually set default keybind to grave key.
* Improve tab completion of /veinminer command
* Add rotated wood to congruence list. This means destruction of rotated wood is fixed

0.5.0
-----
Changes
* References to "shift" have been changed to "sneak" as it is based on sneaking not holding the shift key (if the sneak keybind is not shift).
* /veinminer command gives better output
* Upgraded mod to 1.6
* New stuff
* Basic api for overriding if your tool is adequate
* Client can now choose a preferred mode that is used when joining the game

0.4.1
-----
* Fixes crash

0.4
---
* Inital Release
