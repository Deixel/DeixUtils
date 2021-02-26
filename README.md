# DeixUtils
## Commands
### Nether Portal Calculator
/nethercoords command - calculate the exact coordinates to build a portal in the opposite dimension to perfectly link with a portal at your current coordinates
### Say Coordinates
/saycoords command - send your current X/Y/Z coords to others in chat. Will also be sent to the linked Discord channel
### Config updates 
- /togglediscord command to enable/disable all Discord integrations. OP only
- /config command - update plugin configs on the fly. Includes tab completion! OP only
### Last Death
On death, player will be shown the coordinates they died at. They can then use /lastdeath to see these coordinates again. Coordinates stored until next death or server restart.
## Features
### Discord - Minecraft Chat Linking
Works with my DeixBot discord bot to mirror Minecraft chat and events into a Discord channel and vice versa
- Player Chat
- Player Join/Leave
- Player Death
- Difficulty changes using a sign (see below)
### Player Only Pressure Plates
Wooden Pressure Plates can only be triggered by a player - Animal, Mob and Villager Proof automatic doors!
### Custom Recipies
Adds shapeless recipies to recolour wool - 8 wool and a piece of dye will yield 8 recoloured wool
### Skip Nights Without Everyone In Bed
At night, only 25% (by default) of players in the overworld need to be in bed to skip to morning.
### Endergriefing
Turn off enderman griefing even if doMobGriefing is turned on
### Villager-Proof Wooden Doors
Want to have a house near villagers that they can't get into, but don't want to have an ugly iron door?
Put a torch (configurable) underneath the block the door is on to make it villager-proof. Example:
```
D oor
D oor
F loor
T orch
```
N.B This is a bit buggy and only seems to work in one direction, depending on the rotation of the door.
### Difficulty Signs
Create a sign with [Difficulty] on the first line, and a difficulty (peaceful/easy/normal/hard) on the second line. Right-clicking on this sign lets anyone change the difficulty.
Difficulty is also reset to easy when the last player leaves.