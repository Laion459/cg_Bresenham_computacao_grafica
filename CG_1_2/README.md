# CG_1_2 — Triângulo 3D + Matriz 4×4

Evolução do projeto 2D (Bresenham + Cohen–Sutherland) para **matemática 3D** da Aula 4:

1. **Ponto3D** em coordenadas homogêneas `(x, y, z, w)`
2. **Matriz4x4** — translação, escala e rotação `Rx` / `Ry` / `Rz` (eixo pela **origem**)
3. **Triangulo3D** — mesma matriz nos 3 vértices
4. Projeção (ortográfica ou perspectiva) → Bresenham + clipping no framebuffer

**Ainda não:** rotação por eixo arbitrário (fase 2 do professor).

---

## Ideia

```
Ponto3D / Triangulo3D  →  Matriz4x4 (T, S, Rx, Ry, Rz)
         │
         ▼
   vértices (x,y,z) no espaço
         │
         ▼
   projeção → (sx, sy) na tela
         │
         ▼
   desenhaLinhaClipada (Cohen–Sutherland + Bresenham)
```

---

## Controles

| Tecla | Ação |
|-------|------|
| `W` `A` `S` `D` | Translada em X/Y |
| `R` / `F` | Translada em Z (+/−) |
| `Z` / `X` | Escala ×1.25 / ×0.75 (origem) |
| `Q` / `E` | Rotação em **Z** (±π/16) |
| `T` / `G` | Rotação em **X** |
| `Y` / `H` | Rotação em **Y** |
| `P` | Alterna perspectiva ↔ ortográfica |
| Clique esquerdo | Cria linha 2D overlay (2 cliques) |

Marcadores: **azul** = origem 3D (centro); **vermelho** = ponto demo; eixos RGB = X/Y/Z.

---

## Arquivos novos / principais

```
src/
├── Matriz4x4.java    # 4×4, mul, T/S/Rx/Ry/Rz
├── Ponto3D.java      # homogêneo + projetar
├── Linha3D.java      # segmento A–B
├── Triangulo3D.java  # 3 vértices
├── MainCanvas.java   # cena + teclado + raster
├── Ponto2D.java      # legado 2D (mouse overlay)
└── Linha2D.java      # legado 2D
```

---

## Como executar

```bash
cd CG_1_2
javac -encoding UTF-8 -d bin src/*.java
java -cp bin MainClass
```

(Com `gato.jpg` no diretório de trabalho.)

---

## Fase 2 (depois)

Rotação por eixo qualquer: alinhar eixo com Z, girar, desfazer (slide da aula) — **não** implementado ainda.
