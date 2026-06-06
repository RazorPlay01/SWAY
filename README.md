# Sway

**Realistic foliage interaction and deformation for Minecraft.**

**"Bringing your world to life, one blade of grass at a time."**

---

## 📖 Description

**Make your Minecraft forests feel alive!**

**Sway** is a high-performance mod that makes foliage interact and deform when entities pass through them. No more walking through static, ghost-like grass—with Sway, every step you take and every mob that moves creates a physical reaction in the environment, making the world feel much more immersive and reactive.

Built with performance as the top priority, Sway uses an optimized deformation engine to ensure your FPS remains stable even in the lushest jungles.

## 🌟 Features

- **Dynamic Interaction**: Foliage (grass, flowers, ferns, etc.) deforms and bends away from players and mobs.
- **Optimized Engine**: Uses a high-performance entity-to-block logic that minimizes CPU and GPU overhead.
- **Double-Block Continuity**: Large plants like tall grass and large ferns deform as a single cohesive unit, avoiding visual breaks between the top and bottom halves.
- **Quadratic Bending**: Provides a natural, curved deformation where the base stays rooted and the tip bends gracefully.
- **Developer Friendly**: Includes a simple API for other modders to make their custom plants compatible with Sway.

## ⚙️ Compatibility

- **Minecraft Versions**:
    - Supported versions: **1.20.1**, **1.21.1**, **1.21.11** and **26+**.

- **Modding Platforms**:
    - **Fabric**: ✅ Fully supported!

- **Mod Compatibility**:
    - Works with any mod that uses standard Minecraft foliage models.
    - Custom plants can be registered via the [SwayAPI](src/main/java/com/example/modtemplate/api/SwayAPI.java).

## 📦 Installation

1. **Download the mod**: Grab the latest version from Modrinth or CurseForge.
2. **Install Fabric**: Ensure you have the Fabric Loader installed for your version.
3. **Add the mod**: Place the `.jar` file into your `mods` folder.
4. **Launch & Enjoy**: Open Minecraft and watch the world react to your presence!

## 🛠️ Developer API

Sway is designed to be extensible. If you are a mod developer and want your plants to support Sway deformation, you can use the `SwayAPI`:

```java
// Register your custom block with a 1.2x sway intensity
SwayAPI.register(MyBlocks.CUSTOM_PLANT, 1.2f);
```

Check out our [SwayAPI](src/main/java/com/example/modtemplate/api/SwayAPI.java) for more details.

## 📝 Contributing & Feedback

Contributions and bug reports are welcome!

- Found a bug? Open an issue on our [GitHub](https://github.com/RazorPlay01/SWAY).
- Have an idea? Feel free to open a suggestion thread.
- Want to help? Fork the repo and submit a pull request!

## 📄 License

This mod is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for more details.

## ❤️ Acknowledgments

**Sway** was created to bring a touch of realism to the Minecraft wilderness without sacrificing performance. Thank you for using the mod and supporting the community!
