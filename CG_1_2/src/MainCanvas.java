import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Canvas de renderização com framebuffer próprio.
 * Responsável pelo loop de animação, entrada do usuário e
 * algoritmos de desenho: Bresenham + clipping de segmento
 * (Cohen–Sutherland) para não estourar o buffer.
 * <p>
 * Também traz a lógica do CG_2_1 (triângulo, pivô pC, transformações),
 * mas desenha com {@link #desenhaLinhaClipada} em vez de {@code g.drawLine}.
 */
public class MainCanvas extends JPanel implements Runnable{
	int W = 640;
	int H = 480;
	
	Thread runner;
	boolean ativo = true;
	int paintcounter = 0;
	
	BufferedImage imageBuffer;
	byte bufferDeVideo[];
	
	Random rand = new Random();
	
	byte memoriaPlacaVideo[];
	short paleta[][];
	
	int framecount = 0;
	int fps = 0;
	
	Font f = new Font("", Font.PLAIN, 30);
	
	int clickX = 0;
	int clickY = 0;
	int mouseX = 0;
	int mouseY = 0;
	
	int pixelSize = 0;
	int Largura = 0;
	int Altura = 0;
	
	BufferedImage imgtmp = null;
	
	float posx = 00;
	float posy = 00;
	
	boolean LEFT = false;
	boolean RIGHT = false;
	boolean UP = false;
	boolean DOWN = false;
	
	float filtroR = 1;
	float filtroG = 1;
	float filtroB = 1;
	
	float q1x = 10,q1y = 100;
	float q2x = 10,q2y = 200;

	/**
	 * Lista de segmentos do mundo (o triângulo inicial + linhas criadas com o mouse).
	 * Cada aresta é uma {@link Linha2D}; o triângulo = 3 segmentos fechados.
	 */
	ArrayList<Linha2D> linhas = new ArrayList<Linha2D>();

	/**
	 * Primeiro extremo ao criar linha com clique esquerdo.
	 * null = ainda não clicou o início; não-null = esperando o 2º clique.
	 */
	Ponto2D p0 = null;

	/**
	 * Pivô (ponto azul)  “onde o desenho chumba” na rotação (Q/E).
	 * Clique direito redefine pC. Começa no centro da tela (320, 240).
	 */
	Ponto2D pC = new Ponto2D(320, 240);
	
	/**
	 * Constrói o canvas, inicializa o framebuffer, carrega a imagem
	 * de fundo, cria o triângulo inicial e registra teclado/mouse.
	 */
	public MainCanvas() {
		
		File f = new File("imgbmp.bmp");
		try {
			FileInputStream fin = new FileInputStream(f);

			byte todosodbytes[] = new byte[64000];
			int byteslidos = fin.read(todosodbytes);
			System.out.println("Bytes Lidos "+byteslidos);
			for(int i = 0; i < byteslidos;i++) {
				System.out.println(i+": "+todosodbytes[i]);
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// -----------------------------------------------------------------
		// TRIÂNGULO INICIAL (igual à ideia do CG_2_1)
		// Interação 1: aresta A→B
		// Interação 2: aresta B→C
		// Interação 3: aresta C→A  (fecha o triângulo)
		// Não existe classe Triangulo: são 3 Linha2D na lista "linhas".
		// -----------------------------------------------------------------
		linhas.add(new Linha2D(200, 100, 250, 200)); // A→B
		linhas.add(new Linha2D(250, 200, 150, 200)); // B→C
		linhas.add(new Linha2D(150, 200, 200, 100)); // C→A
		
		setSize(640,480);
		setFocusable(true);
		
		Largura = 640;
		Altura = 480;
		
		pixelSize = 640*480;
		
		
//		try {
//			imgtmp = ImageIO.read(getClass().getResource("fundo.jpg"));
//			System.out.println(""+imgtmp.toString());
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		imgtmp = loadImage("gato.jpg");
		
		imageBuffer = new BufferedImage(640,480, BufferedImage.TYPE_4BYTE_ABGR);
		//imageBuffer.getGraphics().drawImage(imgtmp, 0, 0, null);
		
		
		bufferDeVideo = ((DataBufferByte)imageBuffer.getRaster().getDataBuffer()).getData();
		
		System.out.println("Buffer SIZE "+bufferDeVideo.length );
		
		
//		File f = new File("t1.bmp");
//		try {
//			DataInputStream din = new DataInputStream(new FileInputStream(f));
//			byte b[] = new byte[128];
//			int quant = 0;
//			int cont = 0;
//			while((quant = din.read(b))>=0) {
//				for(int i = 0; i < quant;i++) {
//					System.out.print(""+(b[i]&0xff)+" ");
//				}
//				System.out.println();
//				cont++;
//				if(cont==10) {
//					break;
//				}
//			}
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		
//		for(int i = 0; i < H;i++) {
//			for(int j = 0; j < W;j++) {
//				int pos = (i*W*4)+(j*4);
//				
//				int soma = bufferDeVideo[pos+1]&0xff;
//				soma += bufferDeVideo[pos+2]&0xff;
//				soma += bufferDeVideo[pos+3]&0xff;
//				
//				int media = soma/3;
//				//System.out.println(""+media);
//				
//				bufferDeVideo[pos+1] = (byte)(Math.min((media*20)/100,255)&0x00ff);
//				bufferDeVideo[pos+2] = (byte)(Math.min((media*60)/100,255)&0x00ff);
//				bufferDeVideo[pos+3] = (byte)(Math.min((media*20)/100,255)&0x00ff);
//			}
//		}
		
		//memoriaPlacaVideo = new byte[W*H];
		
		
		/*paleta = new short[255][3];
		
		for(int i = 0; i < 255;i++){
			paleta[i][0] = (short)rand.nextInt(255);
			paleta[i][1] = (short)rand.nextInt(255);
			paleta[i][2] = (short)rand.nextInt(255);
			
		}*/
		
		//Seta Bugfeer com noise
		/*for(int i = 0; i < bufferDeVideo.length;i+=4){
			int r = rand.nextInt(255);
			int g = rand.nextInt(255);
			int b = rand.nextInt(255);
			
			bufferDeVideo[i] = (byte)0x00ff;
			bufferDeVideo[i+1] = (byte)(0x00ff&b);
			bufferDeVideo[i+2] = (byte)(0x00ff&g);
			bufferDeVideo[i+3] = (byte)(0x00ff&r);
		}8?
		
//		// 100,20 200,20
//		for(int i = 0; i < 100;i++){
//			int x = 100+i;
//			int y = 20;
//			int bt = x*4+y*640*4;
//			bufferDeVideo[bt] = (byte)0x00ff;
//			bufferDeVideo[bt+1] = (byte)0;
//			bufferDeVideo[bt+2] = (byte)0;
//			bufferDeVideo[bt+3] = (byte)0x00ff;
//		}
		
		/*for(int y = 0; y < H;y++){
			for(int x = 0; x < W;x++){
				memoriaPlacaVideo[x+y*W] = (byte)((y%255)&0x00ff);
			}
		}*/
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				int key = e.getKeyCode();
				if(key == KeyEvent.VK_W) {
					UP = false;
				}
				if(key == KeyEvent.VK_S) {
					DOWN = false;
				}
				if(key == KeyEvent.VK_A) {
					LEFT = false;
				}
				if(key == KeyEvent.VK_D) {
					RIGHT = false;
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				int key = e.getKeyCode();

				// Interação WASD: translada TODAS as linhas (triângulo + criadas)
				if (key == KeyEvent.VK_W) {
					UP = true;
					// Interação: para cada segmento, translate(0, -10) = sobe
					for (int i = 0; i < linhas.size(); i++) {
						linhas.get(i).translate(0, -10);
					}
				}
				if (key == KeyEvent.VK_S) {
					DOWN = true;
					for (int i = 0; i < linhas.size(); i++) {
						linhas.get(i).translate(0, 10);
					}
				}
				if (key == KeyEvent.VK_A) {
					LEFT = true;
					for (int i = 0; i < linhas.size(); i++) {
						linhas.get(i).translate(-10, 0);
					}
				}
				if (key == KeyEvent.VK_D) {
					RIGHT = true;
					for (int i = 0; i < linhas.size(); i++) {
						linhas.get(i).translate(10, 0);
					}
				}

				// Interação Z/X: escala em relação à ORIGEM (0,0)  como no CG_2_1
				if (key == KeyEvent.VK_Z) {
					for (int i = 0; i < linhas.size(); i++) {
						linhas.get(i).scale(1.25f, 1.25f);
					}
				}
				if (key == KeyEvent.VK_X) {
					for (int i = 0; i < linhas.size(); i++) {
						linhas.get(i).scale(0.75f, 0.75f);
					}
				}

				// Interação Q/E: rotação em torno do pivô pC (clique direito)
				// Sequência da aula "rotação por um ponto escolhido":
				//   1) translate(-pC)  → leva o pivô à origem
				//   2) rotate(θ)       → gira em torno da origem
				//   3) translate(+pC)  → devolve o pivô ao lugar
				if (key == KeyEvent.VK_Q) {
					for (int i = 0; i < linhas.size(); i++) {
						linhas.get(i).translate(-pC.X, -pC.Y);           // passo 1
						linhas.get(i).rotate((float) (Math.PI / 16));     // passo 2
						linhas.get(i).translate(pC.X, pC.Y);              // passo 3
					}
				}
				if (key == KeyEvent.VK_E) {
					for (int i = 0; i < linhas.size(); i++) {
						linhas.get(i).translate(-pC.X, -pC.Y);
						linhas.get(i).rotate((float) (-Math.PI / 16));
						linhas.get(i).translate(pC.X, pC.Y);
					}
				}
			}
		});		
		
		addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// Garante foco de teclado ao clicar (WASD / Q E / Z X)
				requestFocusInWindow();

				clickX = e.getX();
				clickY = e.getY();

				// -------------------------------------------------------------
				// BOTÃO ESQUERDO (1): criar segmento em 2 cliques
				// Interação 1: se p0 == null, guarda o 1º extremo
				// Interação 2: se p0 != null, cria Linha2D(p0 → clique) e zera p0
				// -------------------------------------------------------------
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (p0 == null) {
						// Interação 1: marca o início (linha-guia até o mouse)
						p0 = new Ponto2D(clickX, clickY);
					} else {
						// Interação 2: fecha o segmento e adiciona à lista
						linhas.add(new Linha2D(p0.X, p0.Y, clickX, clickY));
						p0 = null;
					}
				}
				// -------------------------------------------------------------
				// BOTÃO DIREITO (3): redefine o pivô pC (ponto azul)
				// Interação: “chumba” a rotação neste pixel da tela
				// -------------------------------------------------------------
				else if (e.getButton() == MouseEvent.BUTTON3) {
					pC = new Ponto2D(clickX, clickY);
				}

				System.out.println("CLICO " + e.getButton());
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {
				// TODO Auto-generated method stub
				mouseX = arg0.getX();
				mouseY = arg0.getY();
			}
			
			@Override
			public void mouseDragged(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		

		
	}

	/**
	 * Copia uma imagem para o buffer de vídeo aplicando filtros de cor.
	 *
	 * @param image imagem de origem no formato ABGR
	 * @param x     posição horizontal de destino no framebuffer
	 * @param y     posição vertical de destino no framebuffer
	 * @param fr    multiplicador do canal vermelho (0.0 a 1.0+)
	 * @param fg    multiplicador do canal verde (0.0 a 1.0+)
	 * @param fb    multiplicador do canal azul (0.0 a 1.0+)
	 */
	private void drawImageToBuffer(BufferedImage image,int x,int y, float fr, float fg, float fb) {
		byte[] imgBuffer = ((DataBufferByte)image.getRaster().getDataBuffer()).getData();
		
		
		int iw = image.getWidth();
		int ih = image.getHeight();
		
		for(int yi = 0; yi < ih; yi++) {
			for(int xi = 0; xi < iw; xi++) {
				int pixi = yi*iw*4 + xi*4;
				int pixb = (yi+y)*W*4 + (xi+x)*4;
				bufferDeVideo[pixb] = imgBuffer[pixi];
				
				//BW
//				int soma = (imgBuffer[pixi+1]&0xff) + (imgBuffer[pixi+2]&0xff) + (imgBuffer[pixi+3]&0xff);
//				int res = (int)(soma/3);
//				
//				bufferDeVideo[pixb+1] = (byte)(res&0xff);
//				bufferDeVideo[pixb+2] = (byte)(res&0xff);
//				bufferDeVideo[pixb+3] = (byte)(res&0xff);
				
				
//				int b = (imgBuffer[pixi+1]&0xff);
//				int g =	(imgBuffer[pixi+2]&0xff);
//				int r = (imgBuffer[pixi+3]&0xff);
//				
//				int media = (int)255-(((b+g+r)/3));
//				media = Math.min(255, media);
//				
//				b = media;
//				g = media;
//				r = media;
				
				int b = (imgBuffer[pixi+1]&0xff);
				int g =	(imgBuffer[pixi+2]&0xff);
				int r = (imgBuffer[pixi+3]&0xff);
				
				b = (int)(b*fb);
				g = (int)(g*fg);
				r = (int)(r*fr);
//				
				b = Math.min(255, b);
				g = Math.min(255, g);
				r = Math.min(255, r);
				
				bufferDeVideo[pixb+1] = (byte)(b&0xff);
				bufferDeVideo[pixb+2] = (byte)(g&0xff);
				bufferDeVideo[pixb+3] = (byte)(r&0xff);
			}
		}
	}

	/**
	 * Limpa o framebuffer, desenha a cena (fundo, H/V, segmentos clipados,
	 * linha-guia e pivô) e exibe FPS / mouse.
	 *
	 * @param g contexto gráfico fornecido pelo Swing
	 */
	@Override
	public void paint(Graphics g) {
		
		for(int i = 0; i < bufferDeVideo.length; i++) {
			bufferDeVideo[i] = 0;
		}
		
		
		
//		for(int j = 0; j < H;j++) {
//			for(int i = 0; i < W;i++) {
//				int pos = i*4+W*4*j;
//				bufferDeVideo[pos] = (byte)255;
//				bufferDeVideo[pos+1] = (byte)0;
//				bufferDeVideo[pos+2] = (byte)128;
//				bufferDeVideo[pos+3] = (byte)255;
//			}
//		}
//		
//		for(int i = 0; i < 100;i++) {
//			int p0 = 50*4+W*4*100;
//			int pos = p0+i*4;
//			bufferDeVideo[pos] = (byte)255;
//			bufferDeVideo[pos+1] = (byte)255;
//			bufferDeVideo[pos+2] = (byte)0;
//			bufferDeVideo[pos+3] = (byte)0;
//		}
		
		drawImageToBuffer(imgtmp,(int)posx,(int)posy,filtroR,filtroG,filtroB);

		// Auxiliares do template (também clipadas)  úteis para ver o clipping H/V
		desenhaLinhaHorizontal((int)10,(int)100,400);
		desenhaLinhaVertical((int)10,(int)20,200);

		// -----------------------------------------------------------------
		// Interação: desenha CADA segmento da lista com o nosso motor
		// (Cohen–Sutherland + Bresenham), NÃO com g.drawLine.
		// O triângulo = primeiros 3 itens; o resto são linhas do mouse.
		// Se um extremo sai da tela, a linha é PARTIDA na borda.
		// -----------------------------------------------------------------
		for (int i = 0; i < linhas.size(); i++) {
			Linha2D L = linhas.get(i);
			desenhaLinhaClipada(
					(int) L.A.X, (int) L.A.Y,
					(int) L.B.X, (int) L.B.Y,
					0, 0, 0); // preto
		}

		// Interação: linha-guia verde enquanto o 2º clique esquerdo não veio
		// (de p0 até a posição atual do mouse)  também clipada
		if (p0 != null) {
			desenhaLinhaClipada((int) p0.X, (int) p0.Y, mouseX, mouseY, 0, 255, 0);
		}

		// Interação: marca o pivô pC (quadradinho azul 5×5) pixel a pixel no buffer
		desenhaMarcadorPivo((int) pC.X, (int) pC.Y);
		
		
		
		//desenhaLinhaVertical(300,200,200);
		
		// TODO Auto-generated method stub
		//super.paint(g);
		
//		for(int i = 0; i < bufferDeVideo.length;i+=4){
//			int rr = rand.nextInt(255);
//			int gg = rand.nextInt(255);
//			int bb = rand.nextInt(255);
//			
//			bufferDeVideo[i] = (byte)0x00ff;
//			bufferDeVideo[i+1] = (byte)(0x00ff&bb);
//			bufferDeVideo[i+2] = (byte)(0x00ff&gg);
//			bufferDeVideo[i+3] = (byte)(0x00ff&rr);
//		}
		
		/*for(int i = 0; i < memoriaPlacaVideo.length;i++){
			int bufferindex = i*4;
			bufferDeVideo[bufferindex] = (byte)0x00ff;
			bufferDeVideo[bufferindex+1] = (byte)(paleta[memoriaPlacaVideo[i]&0x00ff][2]&0x00ff);
			bufferDeVideo[bufferindex+2] = (byte)(paleta[memoriaPlacaVideo[i]&0x00ff][1]&0x00ff);
			bufferDeVideo[bufferindex+3] = (byte)(paleta[memoriaPlacaVideo[i]&0x00ff][0]&0x00ff);
		}*/
		
		g.setFont(f);
		
		g.setColor(Color.white);
		g.fillRect(0, 0, 640, 480);
//		g.setColor(Color.black);
//		g.drawLine(0, 0, 640, 480);
		
		g.drawImage(imageBuffer,0,0,null);
		
		//g.setColor(Color.BLUE);
		//g.drawLine(clickX, clickY, mouseX, mouseY);
		

		
		g.setColor(Color.black);
		g.drawString("FPS "+fps+" mouse: "+mouseX+","+mouseY
				+"  pC: "+(int)pC.X+","+(int)pC.Y, 10, 25);
	}

	/**
	 * Desenha o marcador do pivô pC (quadrado azul) no framebuffer.
	 * Interação a interação:
	 * 1) Percorre um bloco 5×5 centrado em (cx, cy)
	 * 2) Cada pixel passa por {@link #desenhaPixel} (já com clipping de segurança)
	 * Assim o pivô também não estoura o buffer se estiver perto da borda.
	 *
	 * @param cx centro X do pivô
	 * @param cy centro Y do pivô
	 */
	public void desenhaMarcadorPivo(int cx, int cy) {
		// Interação 1: offsets de -2 .. +2 formam o quadrado 5×5
		for (int dy = -2; dy <= 2; dy++) {
			for (int dx = -2; dx <= 2; dx++) {
				// Interação 2: pixel azul (R=0, G=0, B=255)
				desenhaPixel(cx + dx, cy + dy, 0, 0, 255);
			}
		}
	}

	// =====================================================================
	// CLIPPING DE SEGMENTO  Cohen–Sutherland
	// Janela de recorte = área da tela: [0 .. W-1] × [0 .. H-1]
	// Cada bit do "outcode" marca se o ponto está fora de um lado:
	//   bit 0 (1) = LEFT   (x < 0)
	//   bit 1 (2) = RIGHT  (x > W-1)
	//   bit 2 (4) = BOTTOM (y > H-1)   ← em tela, Y cresce para baixo
	//   bit 3 (8) = TOP    (y < 0)
	// =====================================================================
	private static final int CODE_INSIDE = 0; // 0000  ponto totalmente dentro
	private static final int CODE_LEFT   = 1; // 0001
	private static final int CODE_RIGHT  = 2; // 0010
	private static final int CODE_BOTTOM = 4; // 0100
	private static final int CODE_TOP    = 8; // 1000

	/**
	 * Calcula o código de região (outcode) de um ponto em relação à janela.
	 * Interação a interação:
	 * 1) Começa com INSIDE (nenhum bit ligado).
	 * 2) Se x está à esquerda da janela, liga o bit LEFT.
	 * 3) Se x está à direita da janela, liga o bit RIGHT.
	 * 4) Se y está acima da janela (y &lt; 0), liga o bit TOP.
	 * 5) Se y está abaixo da janela (y &gt; H-1), liga o bit BOTTOM.
	 * Vários bits podem estar ligados ao mesmo tempo (ex.: canto superior esquerdo).
	 *
	 * @param x coordenada X do ponto
	 * @param y coordenada Y do ponto
	 * @return máscara de bits indicando de quais lados o ponto está fora
	 */
	private int calculaOutCode(int x, int y) {
		int code = CODE_INSIDE;

		// Interação 1: testa eixo X contra as bordas laterais
		if (x < 0) {
			code |= CODE_LEFT;          // ponto à esquerda da tela
		} else if (x >= W) {
			code |= CODE_RIGHT;         // ponto à direita da tela
		}

		// Interação 2: testa eixo Y contra as bordas superior/inferior
		// (em coordenadas de tela, y=0 é o topo)
		if (y < 0) {
			code |= CODE_TOP;           // ponto acima da tela
		} else if (y >= H) {
			code |= CODE_BOTTOM;        // ponto abaixo da tela
		}

		return code;
	}

	/**
	 * Recorta (clipa) um segmento de reta contra a janela da tela
	 * usando o algoritmo de Cohen–Sutherland.
	 * <p>
	 * Fluxo de cada iteração do loop:
	 * <ol>
	 *   <li>Calcula outcode dos dois extremos.</li>
	 *   <li><b>Aceitação trivial:</b> ambos os códigos = 0 → segmento
	 *       inteiro dentro → retorna os pontos (possivelmente já ajustados).</li>
	 *   <li><b>Rejeição trivial:</b> (code0 &amp; code1) ≠ 0 → os dois pontos
	 *       estão do mesmo lado de fora → não há pedaço visível → null.</li>
	 *   <li><b>Caso geral:</b> escolhe um extremo que está fora, calcula a
	 *       interseção com a borda correspondente e substitui esse extremo
	 *       pelo ponto da borda. Repete até aceitar ou rejeitar.</li>
	 * </ol>
	 * Assim a linha é “partida”: só o trecho interno sobrevive.
	 *
	 * @param x0 X do ponto inicial (pode estar fora da tela)
	 * @param y0 Y do ponto inicial
	 * @param x1 X do ponto final
	 * @param y1 Y do ponto final
	 * @return array {@code {x0', y0', x1', y1'}} já dentro da janela,
	 *         ou {@code null} se o segmento estiver totalmente fora
	 */
	public int[] clipaSegmentoCohenSutherland(int x0, int y0, int x1, int y1) {
		// Limites da janela de clipping (= resolução do framebuffer)
		final int xmin = 0;
		final int ymin = 0;
		final int xmax = W - 1;
		final int ymax = H - 1;

		// Interação 0: códigos iniciais dos dois extremos
		int code0 = calculaOutCode(x0, y0);
		int code1 = calculaOutCode(x1, y1);

		while (true) {
			// --- Interação A: aceitação trivial ---
			// Nenhum bit ligado nos dois pontos ⇒ ambos dentro da janela.
			if ((code0 | code1) == 0) {
				return new int[] { x0, y0, x1, y1 };
			}

			// --- Interação B: rejeição trivial ---
			// AND bit a bit ≠ 0 ⇒ existe pelo menos um lado em que
			// OS DOIS pontos estão fora ao mesmo tempo (ex.: ambos à esquerda).
			// Nesse caso a reta inteira fica fora e não precisa desenhar nada.
			if ((code0 & code1) != 0) {
				return null;
			}

			// --- Interação C: precisa cortar ---
			// Escolhe um extremo que ainda está fora (preferimos code0 se ≠ 0).
			int codeOut = code0 != 0 ? code0 : code1;

			// Variáveis da interseção com a borda escolhida
			int x = 0;
			int y = 0;

			// Interação C1: se o ponto fora está ACIMA (y < 0), corta com y = ymin
			if ((codeOut & CODE_TOP) != 0) {
				// Parametrização da reta: x = x0 + (x1-x0) * (ymin-y0)/(y1-y0)
				x = x0 + (int) Math.round((double) (x1 - x0) * (ymin - y0) / (double) (y1 - y0));
				y = ymin;
			}
			// Interação C2: se está ABAIXO (y > ymax), corta com y = ymax
			else if ((codeOut & CODE_BOTTOM) != 0) {
				x = x0 + (int) Math.round((double) (x1 - x0) * (ymax - y0) / (double) (y1 - y0));
				y = ymax;
			}
			// Interação C3: se está à DIREITA (x > xmax), corta com x = xmax
			else if ((codeOut & CODE_RIGHT) != 0) {
				y = y0 + (int) Math.round((double) (y1 - y0) * (xmax - x0) / (double) (x1 - x0));
				x = xmax;
			}
			// Interação C4: se está à ESQUERDA (x < xmin), corta com x = xmin
			else if ((codeOut & CODE_LEFT) != 0) {
				y = y0 + (int) Math.round((double) (y1 - y0) * (xmin - x0) / (double) (x1 - x0));
				x = xmin;
			}

			// Interação D: substitui o extremo que estava fora pelo ponto da borda
			// e recalcula o outcode desse extremo para a próxima volta do loop.
			if (codeOut == code0) {
				x0 = x;
				y0 = y;
				code0 = calculaOutCode(x0, y0);
			} else {
				x1 = x;
				y1 = y;
				code1 = calculaOutCode(x1, y1);
			}
			// Volta ao início do while: tenta aceitar, rejeitar ou cortar de novo.
		}
	}

	/**
	 * Desenha uma linha já passando pelo clipping de segmento.
	 * Interação a interação:
	 * 1) Chama Cohen–Sutherland para obter só o pedaço visível.
	 * 2) Se retornou null, a linha está toda fora → não desenha (nem toca o buffer).
	 * 3) Se retornou pontos, chama Bresenham só com esse segmento interno.
	 *
	 * @param x0 X inicial (pode estar fora)
	 * @param y0 Y inicial
	 * @param x1 X final
	 * @param y1 Y final
	 * @param r  vermelho 0–255
	 * @param g  verde 0–255
	 * @param b  azul 0–255
	 */
	public void desenhaLinhaClipada(int x0, int y0, int x1, int y1, int r, int g, int b) {
		// Interação 1: parte a linha nas bordas da tela
		int[] clipado = clipaSegmentoCohenSutherland(x0, y0, x1, y1);

		// Interação 2: rejeição → nada a desenhar, buffer intacto
		if (clipado == null) {
			return;
		}

		// Interação 3: rasteriza apenas o trecho [x0',y0'] → [x1',y1'] já válido
		desenhaLinhaBresenham(clipado[0], clipado[1], clipado[2], clipado[3], r, g, b);
	}

	/**
	 * Desenha uma linha horizontal no buffer de vídeo.
	 * Usa clipping de segmento: converte (x, y, comprimento) em dois extremos
	 * e só desenha o trecho que resta dentro da janela.
	 *
	 * @param x coordenada X inicial
	 * @param y coordenada Y (fixa)
	 * @param w comprimento da linha em pixels
	 */
	public void desenhaLinhaHorizontal(int x, int y, int w) {
		// Interação 1: extremos do segmento horizontal
		int x0 = x;
		int y0 = y;
		int x1 = x + w - 1; // último pixel inclusivo do comprimento w
		int y1 = y;

		// Interação 2: clipa e, se houver pedaço visível, pinta pixel a pixel
		int[] clipado = clipaSegmentoCohenSutherland(x0, y0, x1, y1);
		if (clipado == null) {
			return; // totalmente fora → não escreve no buffer
		}

		int xa = clipado[0];
		int ya = clipado[1];
		int xb = clipado[2];
		// ya == yb após clip horizontal; usamos ya

		// Garante ordem esquerda → direita
		if (xa > xb) {
			int tmp = xa;
			xa = xb;
			xb = tmp;
		}

		// Interação 3: escreve só pixels já dentro da tela (pretos)
		for (int xi = xa; xi <= xb; xi++) {
			desenhaPixel(xi, ya, 0, 0, 0);
		}
	}

	/**
	 * Desenha uma linha vertical no buffer de vídeo.
	 * Mesma ideia da horizontal: clipa o segmento e só então escreve.
	 *
	 * @param x coordenada X (fixa)
	 * @param y coordenada Y inicial
	 * @param h altura da linha em pixels
	 */
	public void desenhaLinhaVertical(int x, int y, int h) {
		// Interação 1: extremos do segmento vertical
		int x0 = x;
		int y0 = y;
		int x1 = x;
		int y1 = y + h - 1;

		// Interação 2: clipping de segmento
		int[] clipado = clipaSegmentoCohenSutherland(x0, y0, x1, y1);
		if (clipado == null) {
			return;
		}

		int xa = clipado[0];
		int ya = clipado[1];
		int yb = clipado[3];

		if (ya > yb) {
			int tmp = ya;
			ya = yb;
			yb = tmp;
		}

		// Interação 3: escreve o trecho visível em vermelho
		for (int yi = ya; yi <= yb; yi++) {
			desenhaPixel(xa, yi, 255, 0, 0);
		}
	}

	/**
	 * Acende um pixel no buffer de vídeo no formato ABGR.
	 * Rede de segurança: mesmo após o clipping de segmento, ignora
	 * coordenadas inválidas para nunca estourar o array.
	 *
	 * @param x coordenada X do pixel
	 * @param y coordenada Y do pixel
	 * @param r canal vermelho (0–255)
	 * @param g canal verde (0–255)
	 * @param b canal azul (0–255)
	 */
	public void desenhaPixel(int x, int y, int r, int g, int b) {
		// Interação 1: rejeita imediatamente se estiver fora (não indexa o buffer)
		if (x < 0 || x >= W || y < 0 || y >= H) {
			return;
		}

		// Interação 2: calcula o índice linear no framebuffer ABGR (4 bytes/pixel)
		int pospix = y * (W * 4) + x * 4;

		// Interação 3: grava Alpha, Blue, Green, Red
		bufferDeVideo[pospix] = (byte) 255;
		bufferDeVideo[pospix + 1] = (byte) (b & 0xff);
		bufferDeVideo[pospix + 2] = (byte) (g & 0xff);
		bufferDeVideo[pospix + 3] = (byte) (r & 0xff);
	}

	/**
	 * Desenha um segmento de reta entre dois pontos usando o algoritmo
	 * de Bresenham, com apenas operações inteiras.
	 * <p>
	 * Pré-condição desejável: os pontos já passaram por
	 * {@link #clipaSegmentoCohenSutherland} (via {@link #desenhaLinhaClipada}),
	 * garantindo que não saem da janela.
	 *
	 * @param x0 coordenada X do ponto inicial
	 * @param y0 coordenada Y do ponto inicial
	 * @param x1 coordenada X do ponto final
	 * @param y1 coordenada Y do ponto final
	 * @param r  canal vermelho (0–255)
	 * @param g  canal verde (0–255)
	 * @param b  canal azul (0–255)
	 */
	public void desenhaLinhaBresenham(int x0, int y0, int x1, int y1, int r, int g, int b) {
		// Interação 1: deltas absolutos (tamanho do deslocamento)
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		// Interação 2: sentido do passo em cada eixo (+1 ou -1)
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;

		// Interação 3: parâmetro de decisão inicial do Bresenham
		int erro = dx - dy;

		int x = x0;
		int y = y0;

		while (true) {
			// Interação 4: acende o pixel atual
			desenhaPixel(x, y, r, g, b);

			// Interação 5: chegou ao destino → encerra
			if (x == x1 && y == y1) {
				break;
			}

			// Interação 6: dobra o erro para comparar sem frações
			int erro2 = 2 * erro;

			// Interação 7: se o erro “puxa” para o eixo X, anda em X
			if (erro2 > -dy) {
				erro -= dy;
				x += sx;
			}
			// Interação 8: se o erro “puxa” para o eixo Y, anda em Y
			// (os dois ifs podem ser verdadeiros → passo diagonal)
			if (erro2 < dx) {
				erro += dx;
				y += sy;
			}
		}
	}

	/**
	 * Inicia a thread responsável pelo loop de simulação e renderização.
	 */
	public void start(){
		runner = new Thread(this);
		runner.start();
	}
	
	int timer = 0;

	/**
	 * Atualiza o estado do mundo com base no tempo decorrido:
	 * movimento da imagem, filtros de cor e posições auxiliares.
	 *
	 * @param diftime tempo decorrido desde o último frame, em milissegundos
	 */
	public void simulaMundo(long diftime){
		
		float difS = diftime/1000.0f;
		float vel = 50;
		
		timer+=diftime;
		if(timer>=1000) {
			timer = 0;
			filtroR = rand.nextFloat();
			filtroG = rand.nextFloat();
			filtroB = rand.nextFloat();
		}
		
		if(UP) {
			// WASD agora move a geometria (triângulo); fundo fica parado
		}
		if(DOWN) {
		}
		if(LEFT) {
		}
		if(RIGHT) {
		}
		
		q1x+=0.2;
		//q2x=q2x+100*diftime/1000.0f;
		float dx = mouseX-q2x;
		float dy = mouseY-q2y;
		
		double ang = Math.atan2(dy, dx);
		
		q2x = (float)(q2x+Math.cos(ang)*100*diftime/1000.0f);
		q2y = (float)(q2y+Math.sin(ang)*100*diftime/1000.0f);
	}

	/**
	 * Loop principal da thread: simula o mundo, solicita o repaint
	 * e calcula o FPS a cada segundo.
	 */
	@Override
	public void run() {
		long time = System.currentTimeMillis();
		long segundo = time/1000;
		long diftime = 0;
		while(ativo){
			simulaMundo(diftime);
			paintImmediately(0, 0, 640, 480);
			paintcounter+=100;
			
			try {
				Thread.sleep(0);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long newtime = System.currentTimeMillis();
			long novoSegundo = newtime/1000;
			diftime = System.currentTimeMillis() - time;
			time = System.currentTimeMillis();
			framecount++;
			if(novoSegundo!=segundo) {	
				fps = framecount;
				framecount = 0;
				segundo = novoSegundo;
			}
		}
	}

	/**
	 * Carrega uma imagem do disco e converte para o formato ABGR
	 * usado pelo framebuffer.
	 *
	 * @param filename caminho do arquivo de imagem
	 * @return imagem convertida em {@link BufferedImage}, ou {@code null} em caso de erro
	 */
	public BufferedImage loadImage(String filename) {
		try {
			imgtmp = ImageIO.read(new File(filename));
			
			BufferedImage imgout = new BufferedImage(imgtmp.getWidth(), imgtmp.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			
			imgout.getGraphics().drawImage(imgtmp, 0, 0, null);
			
			imgtmp = null;
			
			return imgout;
		} catch (IOException e1) {
			e1.printStackTrace();
			return null;
		}
	}
}
