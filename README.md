<p align="center">
  <img src="240px-Hungry_Flying_Fish_Skin_(White_Fish_Black_Axolotl).png" alt="Autorule icon" width="64" height="64">
</p>

# Autorule

A tiny client-side Fabric mod that makes Hypixel Autopet messages easier to read at a glance.

When Autopet equips a pet, Autorule rewrites the chat message into a compact format like:

```text
Petswap! >> lvl100 Black Cat
```

It preserves pet name colors where possible and cleans up extra visual noise.

## Examples

<p align="center">
  <img src="chat_message_flyingfish.png" alt="Flying Fish Autopet message example">
</p>

<p align="center">
  <img src="chat_message_mosquito.png" alt="Mosquito Autopet message example">
</p>

<p align="center">
  <img src="chat_message_rosedrag.png" alt="Golden Dragon Autopet message example">
</p>

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.19.3+
- Java 21
- Fabric API

## Build

```sh
gradle build
```

The built jar will be in `build/libs/`.

## License

Source available for personal, non-commercial use only. See [LICENSE](LICENSE).
