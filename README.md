<h2 align="center">
Stairstone<br/><br/>
<img width="160" height="160" src="src/main/resources/assets/stairstone/icon320.png"/>
</h2>

### Allows stairs and slabs to control the directions in which Redstone connects and powers

> [!IMPORTANT]
>
> This mod will not work with [Lithium](https://modrinth.com/mod/lithium) unless `mixin.block.redstone_wire=false` is set in its config.
>
> This mod may not work with [Carpet](https://modrinth.com/mod/carpet), any Carpet addons, or any other mod that mixins to Redstone behaviour.

## Directional upwards Redstone blocking
Slabs and stairs will now block Redstone going up if the bottom or side face in that direction is fully covered

Screenshots:
<details>
<summary>Blocking with a slab</summary>

![A Redstone line unable to connect to the neighbouring Redstone line that is one block up, because a slab is in the way](screenshots/blocking_with_slab.png)
</details>
<details>
<summary>Blocking with stairs</summary>

![A Redstone line going up a block in one direction, the other direction is blocked by a stair block](screenshots/blocking_with_stair.png)
</details>

## Directional downwards Redstone
Redstone can now travel down stairs on the sides which have full faces
<details>
<summary>Screenshot</summary>

![A Redstone signal going down the face of a stair block on the left, but not on the right as there's no full face in that direction](screenshots/downward_directional.png)
</details>

## Directional powering
A Redstone signal running into a stair block will only power the sides that have a full face

> [!NOTE]
>
> Changing the shape of a stair block using a neighbouring stair block does NOT send an update to the powered blocks, or the blocks to be powered. This is a limitation of the mod, but it also adds another way to have blocks in a BUD'ed state, i.e. they are unpowered/powered when they receive the next neighbour update.

<details>
<summary>Powering a lamp</summary>

![A Redstone line running into a stair block. The stair block has a full face on the top and in the direction facing the Redstone wire. There are three Redstone lamps surrounding the stair block, and only the one on top is powered.](screenshots/lamps_directional.png)
![A Redstone line running into a corner stair block. The stair block has a full face on the top, in the direction facing the Redstone wire, and on the right side. There are two powered Redstone lamps surrounding the stair block, both are on the side of the stair block with the full face.](screenshots/lamps_directional_2.png)
</details>
<details>
<summary>Powering a Redstone line</summary>

![A Redstone line running into the front side of a corner stair block. The stair block has a full face on the top, front, and right side. There is a Redstone line on top, and the two sides of the stair block. Only the Redstone lines on the top and right side are powered. The top Redstone line is powering a Redstone lamp](screenshots/repeater_wire_directional.png)
</details>

## Block tag
Mods and data packs can change which blocks affect Redstone using the following block tags, or clear the tag to disable it entirely:
- `stairstone:directional_redstone_connects` (directional blocking and downwards)
- `stairstone:directional_redstone_power` (directional powering)
