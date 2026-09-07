# Bresenham + Clipping + Triângulo / Pivô  Computação Gráfica

Projeto `CG_1_2`: rasterização **manual** no framebuffer Java, unindo:

1. **Bresenham**  desenha a reta só com inteiros  
2. **Cohen–Sutherland**  clipping de segmento (parte a linha na borda; não estoura o buffer)  
3. **Lógica do CG_2_1**  triângulo (3 segmentos), pivô azul `pC`, transformações  

Diferença central em relação ao código do professor no M2: **não** usamos `g.drawLine`.  
Tudo passa por `desenhaLinhaClipada` → clip + Bresenham no `bufferDeVideo[]`.

---

## Ideia geral (ordem de estudo)

```
Triângulo / linhas  →  transformações (WASD, Z/X, Q/E em torno de pC)
         │
         ▼
   extremos A,B (float, podem sair da tela)
         │
         ▼
   desenhaLinhaClipada
         │
         ├─► Cohen–Sutherland  (parte o segmento)
         └─► Bresenham         (só o pedaço interno → buffer)
```

Leia nesta sequência no código:

1. `Ponto2D`  translate / scale / rotate  
2. `Linha2D`  aplica T em A e B  
3. `MainCanvas`  triângulo, mouse, teclado, `paint`  
4. `clipaSegmentoCohenSutherland`  clipping  
5. `desenhaLinhaBresenham`  raster  

---

## Controles

| Entrada | Ação |
|---------|------|
| `W` `A` `S` `D` | Translada **todas** as linhas (triângulo incluso) |
| `Z` / `X` | Escala ×1.25 / ×0.75 (em relação à origem) |
| `Q` / `E` | Rotação ±π/16 **em torno do pivô pC** |
| Clique **esquerdo** | 1º = início, 2º = fim → nova `Linha2D` |
| Clique **direito** | Define o pivô azul `pC` (“chumba” a rotação) |
| Mouse (com 1º clique feito) | Linha-guia verde até o cursor |

---

## O que aparece na tela

| Elemento | Como é desenhado |
|----------|------------------|
| Triângulo preto | 3 `Linha2D` iniciais + `desenhaLinhaClipada` |
| Linhas criadas | Mesma lista `linhas` |
| Linha-guia verde | `p0` → mouse, clipada |
| Quadrado azul | Marcador de `pC` via `desenhaMarcadorPivo` |
| H / V auxiliares | Template; também clipadas |
| Fundo `gato.jpg` | `drawImageToBuffer` (filtros RGB periódicos) |

**Teste de clipping:** mova o triângulo com WASD até sair da tela, ou rotacione com Q/E. A aresta deve **parar na borda**, sem crash.

---

## Arquivos

```
CG_1_2/
├── src/
│   ├── MainClass.java     # Janela Swing + start()
│   ├── MainCanvas.java    # Framebuffer, clip, Bresenham, input, cena
│   ├── Ponto2D.java       # Ponto + transformações
│   └── Linha2D.java       # Segmento A–B (sem Graphics.drawLine)
├── gato.jpg
└── README.md
```

---

## Como executar

Na pasta `CG_1_2` (com `gato.jpg` no working directory):

```bash
cd "Z:\LEONARDO\FACU\computação grafica\M1 parte 2\job 1 pre job\ComputacaoGrafica2026\CG_1_2"

javac -encoding UTF-8 -d bin src\MainClass.java src\MainCanvas.java src\Ponto2D.java src\Linha2D.java
java -cp bin MainClass
```

---

## Catálogo de classes / métodos

### `MainClass`
| Método | Função |
|--------|--------|
| `main` | Cria `JFrame`, adiciona `MainCanvas`, inicia o loop |

### `Ponto2D`
| Método | Função |
|--------|--------|
| `Ponto2D(x,y)` | Guarda coordenadas |
| `translate(dx,dy)` | `(x,y) → (x+dx, y+dy)` |
| `scale(sx,sy)` | `(x,y) → (x·sx, y·sy)` em relação à origem |
| `rotate(ang)` | Rotação da aula: `x' = x cos + y sin`, `y' = -x sin + y cos` |

### `Linha2D`
| Método | Função |
|--------|--------|
| `Linha2D(x1,y1,x2,y2)` | Cria extremos A e B |
| `translate` / `scale` / `rotate` | Aplica a transformação nos **dois** extremos |

### `MainCanvas`  cena / input
| Método | Função |
|--------|--------|
| construtor | Framebuffer, triângulo (3 arestas), listeners |
| `keyPressed` | WASD / Z X / Q E sobre a lista `linhas` |
| `mousePressed` | Esquerdo = criar linha; direito = `pC` |
| `paint` | Limpa buffer → fundo → H/V → linhas clipadas → guia → pivô → blit |
| `desenhaMarcadorPivo` | Quadrado azul 5×5 no buffer |
| `simulaMundo` / `run` | Filtros, FPS, loop |
| `loadImage` / `drawImageToBuffer` | Fundo ABGR |

### `MainCanvas`  clipping + raster
| Método | Função |
|--------|--------|
| `calculaOutCode` | Bits LEFT/RIGHT/TOP/BOTTOM do ponto |
| `clipaSegmentoCohenSutherland` | Parte o segmento; `int[]` ou `null` |
| `desenhaLinhaClipada` | Clip → Bresenham |
| `desenhaLinhaBresenham` | Raster só com inteiros |
| `desenhaPixel` | 1 pixel ABGR (+ segurança de bounds) |
| `desenhaLinhaHorizontal` / `Vertical` | Auxiliares já clipadas |

---

## Rotação em torno de `pC` (Q/E)

Igual ao slide *“Rotação por um ponto escolhido”* e ao `CG_2_1`:

1. `translate(-pC.X, -pC.Y)`  pivô vai para a origem  
2. `rotate(±π/16)`  gira  
3. `translate(+pC.X, +pC.Y)`  pivô volta  

O clique direito só muda **onde** está esse pivô.

---

## Por que não `g.drawLine`?

| | Professor (`CG_2_1`) | Este trabalho (`CG_1_2`) |
|--|---------------------|-------------------------|
| Desenho | `Graphics.drawLine` | Buffer + Bresenham |
| Clipping | Nativo do Java | Cohen–Sutherland (implementado) |
| Triângulo / `pC` | Sim | Sim (mesma ideia) |

O clipping nativo “esconde” o algoritmo. Aqui o pedaço de fora **não entra** no buffer  requisito da aula.

---

## Referências

- Bresenham (1965)  
- Cohen–Sutherland line clipping  
- Transformações 2D  Foley & Van Dam / material da disciplina  
- Base M2: `CG_2_1` (triângulo + pivô)

---

## Autor

**Leonardo**  atividade acadêmica de Computação Gráfica
