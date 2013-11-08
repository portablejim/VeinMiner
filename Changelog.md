Changelog
=========

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
