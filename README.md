# Algoritmo de Bresenham — Computação Gráfica

Implementação do **Algoritmo de Bresenham** para rasterização de segmentos de reta em um framebuffer Java, desenvolvida como atividade da disciplina de Computação Gráfica.

O projeto parte de uma base fornecida em aula e estende o motor de desenho com suporte a linhas em **qualquer inclinação**, utilizando apenas operações inteiras — sem dependência de APIs gráficas de alto nível para o traçado da reta.

---

## Demonstração

| Elemento | Descrição |
|----------|-----------|
| Linha vermelha | Segmento fixo `(10, 100) → (400, 300)` desenhado com Bresenham |
| Linha verde | Segmento dinâmico do ponto clicado até a posição do mouse |
| Linhas auxiliares | Traços horizontal e vertical (implementação original do template) |

---

## Funcionalidades

- Rasterização de pixels diretamente em `BufferedImage` (`TYPE_4BYTE_ABGR`)
- Algoritmo de Bresenham para linhas obliqüas em todas as direções
- Funções auxiliares para linhas horizontais e verticais
- Renderização de imagem de fundo com filtros de cor dinâmicos
- Loop de renderização com exibição de FPS e coordenadas do mouse
- Controles de movimento da imagem de fundo via teclado

---

## Tecnologias

- **Java** (SE)
- **Java AWT / Swing** — janela, eventos e canvas
- **Eclipse IDE** — ambiente de desenvolvimento recomendado

---

## Estrutura do repositório

```
.
├── CG_1/          # Template inicial da disciplina
├── CG_1_2/        # Projeto com implementação do Bresenham ★
│   ├── src/
│   │   ├── MainClass.java    # Ponto de entrada da aplicação
│   │   └── MainCanvas.java   # Framebuffer, loop e algoritmos de desenho
│   └── gato.jpg              # Imagem de fundo utilizada na cena
└── README.md
```

> O diretório **`CG_1_2`** contém a implementação principal deste repositório.

---

## Pré-requisitos

- [JDK 8+](https://adoptium.net/) instalado e configurado no `PATH`
- [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/) *(recomendado)*

---

## Como executar

### Eclipse

1. Clone o repositório:
   ```bash
   git clone https://github.com/Laion459/cg_Bresenham_computacao_grafica.git
   ```
2. Abra o Eclipse e selecione **File → Open Projects from File System…**
3. Importe a pasta `CG_1_2`
4. Certifique-se de que `gato.jpg` está na raiz do projeto `CG_1_2`
5. Execute a classe `MainClass`

### Linha de comando

Na pasta `CG_1_2`:

```bash
javac -d bin src/MainClass.java src/MainCanvas.java
java -cp bin MainClass
```

---

## Controles

| Entrada | Ação |
|---------|------|
| `W` / `A` / `S` / `D` | Move a imagem de fundo |
| Clique do mouse | Define o ponto inicial da linha verde |
| Movimento do mouse | Atualiza o ponto final da linha verde em tempo real |

---

## Implementação do Bresenham

O método `desenhaLinhaBresenham` recebe dois pontos `(x0, y0)` e `(x1, y1)` e percorre o segmento pixel a pixel:

1. Calcula `dx`, `dy` e as direções de incremento (`sx`, `sy`)
2. Inicializa a variável de erro: `erro = dx - dy`
3. A cada iteração, plota o pixel atual via `desenhaPixel`
4. Atualiza `x` e/ou `y` conforme o critério de erro de Bresenham
5. Encerra ao atingir `(x1, y1)`

```java
public void desenhaLinhaBresenham(int x0, int y0, int x1, int y1, int r, int g, int b)
```

Cada pixel é escrito diretamente no array de bytes do framebuffer (`bufferDeVideo`), respeitando o formato **ABGR** de 4 bytes por pixel.

---

## Arquitetura de renderização

```
MainClass
    └── MainCanvas (JPanel + Runnable)
            ├── bufferDeVideo[]     ← framebuffer em memória
            ├── desenhaPixel()      ← escrita atômica de um pixel
            ├── desenhaLinhaBresenham()
            ├── drawImageToBuffer() ← composição da imagem de fundo
            └── paint()             ← limpa, desenha e exibe o buffer
```

---

## Referências

- Bresenham, J. E. — *Algorithm for computer control of a digital plotter* (1965)
- Material base: repositório da disciplina de Computação Gráfica — UNIVALI

---

## Autor

**Leonardo** — [Laion459](https://github.com/Laion459)

---

## Licença

Projeto acadêmico. Consulte o professor responsável pela disciplina para orientações sobre uso e distribuição.
