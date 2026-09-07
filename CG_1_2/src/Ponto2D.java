/**
 * Ponto 2D com coordenadas flutuantes e transformações geométricas
 * da aula (translação, escala e rotação em torno da origem).
 * <p>
 * Cada transformação altera o próprio ponto (efeito colateral),
 * como no código do professor em CG_2_1.
 */
public class Ponto2D {
	/** Coordenada horizontal (pode sair da tela; o clipping cuida na hora de desenhar). */
	float X;
	/** Coordenada vertical (em tela, Y cresce para baixo). */
	float Y;

	/**
	 * Cria um ponto nas coordenadas dadas.
	 *
	 * @param x posição X inicial
	 * @param y posição Y inicial
	 */
	public Ponto2D(float x, float y) {
		// Interação 1: guarda as coordenadas no objeto
		X = x;
		Y = y;
	}

	/**
	 * Translação: (x, y) → (x + dx, y + dy).
	 * Interação a interação:
	 * 1) Soma dx em X
	 * 2) Soma dy em Y
	 * Não precisa de matriz 2×2; é só deslocamento.
	 *
	 * @param dx quanto andar no eixo X (positivo = direita)
	 * @param dy quanto andar no eixo Y (positivo = para baixo na tela)
	 */
	public void translate(float dx, float dy) {
		// Interação 1: desloca X
		X = X + dx;
		// Interação 2: desloca Y
		Y = Y + dy;
	}

	/**
	 * Escala em relação à origem (0, 0): (x, y) → (x·sx, y·sy).
	 * Interação a interação:
	 * 1) Multiplica X pelo fator sx
	 * 2) Multiplica Y pelo fator sy
	 * Se o ponto não está na origem, ele também “afasta/aproxima” da origem
	 * (como no slide da aula). Fator negativo = reflexão naquele eixo.
	 *
	 * @param sx fator em X (ex.: 1.25 cresce, 0.75 encolhe)
	 * @param sy fator em Y
	 */
	public void scale(float sx, float sy) {
		// Interação 1: escala horizontal
		X = X * sx;
		// Interação 2: escala vertical
		Y = Y * sy;
	}

	/**
	 * Rotação em torno da origem (0, 0), fórmula da aula (Foley / CG_2_1):
	 *   x' =  x·cos(θ) + y·sin(θ)
	 *   y' = -x·sin(θ) + y·cos(θ)
	 * Interação a interação:
	 * 1) Lê X e Y atuais
	 * 2) Calcula nX com a 1ª fórmula (usa X e Y originais)
	 * 3) Calcula nY com a 2ª fórmula (ainda com X e Y originais  por isso usa nX/nY)
	 * 4) Grava nX e nY de volta no ponto
	 * Ângulo em radianos (ex.: Math.PI/16).
	 *
	 * @param ang ângulo θ em radianos
	 */
	public void rotate(float ang) {
		// Interação 1–2: novo X (não sobrescreve X ainda, senão estraga o cálculo de Y)
		float nX = (float) (X * Math.cos(ang) + Y * Math.sin(ang));
		// Interação 3: novo Y com os valores ANTIGOS de X e Y
		float nY = (float) (-X * Math.sin(ang) + Y * Math.cos(ang));
		// Interação 4: atualiza o ponto
		X = nX;
		Y = nY;
	}
}
