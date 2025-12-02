# PROMPTBOOK PRO – Java · LibGDX · TeaVM · Shader Engine · Continue.dev
High-level prompt library for structured coding with Continue.dev  
Author: Pavel (project owner)  
Target: Java 23 · LibGDX · TeaVM (Web) · Gradle · GLSL ES2

---

# ================================================================
#  0. SYSTEM RULES (Always apply)
# ================================================================
When generating or modifying code:

- Use **Java 23**, prefer records, sealed types, enhanced switch.
- LibGDX: separate **update()** and **render()** responsibilities.
- TeaVM: avoid threads, reflection, File IO, Java NIO, unsafe APIs.
- Shaders: WebGL1 / GLSL ES 2.0 compatible.
- AssetManager: never load inside render(), always preload.
- Provide FULL working code (no snippets unless asked).
- Never invent project file paths — infer from context or ask 1–2 questions.
- Reduce boilerplate unless necessary for clarity.
- Always optimize for readability.

---

