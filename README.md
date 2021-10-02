# LD49
this project is a submission to the Ludum Dare 49 Compo, that or a complete failure.

## IDEAS
### Theme: unstable
##### definition
Tending strongly to change

Not constant; fluctuating

- tile-based 2d game, cracking/shaking tiles, that get destroyed
- falling left/right, have to actively balance (or anything that you need to actively rebalance)
- building something that you have to maintain
- reaction based game + decision between maintaining and expanding?
- tricky towers?

## final? idea:
tile-based 2d game, rougly linear straight line forward
procedurally generated enemies
continuous movement
turret rooted enemies that shoot bullet-hell projectiles at the player
dash
not instant death? 

## Rendering the player

- particle positions and sizes determined by a SSBO that gets modified by a compute shader
- render normal -1..1 VAO instanced
- fragment shader does magic

## TODO

- [x] render/store tiles in a queue
- [x] move player
- [x] start destroying tiles when standing on them
- [x] render player/hp bar
- [x] render enemies on ground
- [x] add projectiles/do collision
- [ ] enemy spawning
- [ ] add fraction of player vel to particle velocities
- [ ] more burning/blending
- [ ] sounds
- [ ] add enemies "rooted" to ground
- [ ] dash
- [ ] ???
- [ ] win jam