# Lovely Snails Changelog

## 1.0.0

- Initial Release.
- Added the Snail entity.
  - Can be tamed with mushrooms.
  - Once tamed, it can be taken care of to grow into an adult.
  - Adult snails can carry players and items if equipped with chests.
  - Items in a snail inventory are ordered by pages, which are associated to a chest.
  - Snail inventory pages can be browsed via scrolling in the storage space.
- Added Snail Spawn Egg.

### 1.0.1

- Added French translations.
- Added Simplified Chinese translations ([#2](https://github.com/LambdAurora/lovely_snails/pull/2)).
- Fixed food not being consumed by snails.

### 1.0.2

- Fixed shift-click behavior in snail inventory.
  - Fixed chests being transferable to baby snails.

### 1.0.3

- Added inventory page buttons to improve accessibility.
- Fixed baby snails collision box.

### 1.0.4

- Updated to 1.18.2.
- Added snail spawn biome tags:
  - `lovely_snails:snail_spawn` for a spawn weight of 8
  - `lovely_snails:swamp_like_spawn` for a spawn weight of 10

## 1.1.0

- Updated to 1.19.
- Added `locked` NBT tag for snails, allow to entirely lock movement and the inventory to the owner only.
- Added Russian translations ([#9](https://github.com/LambdAurora/lovely_snails/pull/9)).
- Added Canadian French translations.
- Moved the Snail Spawn Egg to be in the same place as Vanilla would put Spawn Eggs in the creative inventory.
- Stopped rendering chests if the snail is a baby. Not naturally possible, but it happened.

### 1.1.1

- Updated to 1.19.3.
- Improved a little the spawn logic code.

### 1.1.2

- Updated to 1.20.1.
- Added moss blocks and moss carpets as valid spawn blocks for snails.
- Added hanging roots, melon seeds, pumpkin seeds, torchflower seeds, and wheat seeds as snail food.

### 1.1.3

- Fixed adult snails appearing small after reconnect due to network desynchronizations
  ([#13](https://github.com/LambdAurora/lovely_snails/issues/13), [#14](https://github.com/LambdAurora/lovely_snails/issues/14)).
