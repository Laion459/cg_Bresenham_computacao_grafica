/**
 * Segmento de reta 2D definido por dois {@link Ponto2D} (A e B).
 * <p>
 * Transformações são aplicadas nos dois extremos  ideia da aula:
 * se T transforma o segmento (A,B), então A' = T(A) e B' = T(B).
 * <p>
 * O desenho NÃO usa {@code Graphics.drawLine}. Quem rasteriza é o
 * {@link MainCanvas} com clipping + Bresenham no framebuffer.
 */
public class Linha2D {
	/** Extremo inicial do segmento. */
	Ponto2D A;
	/** Extremo final do segmento. */
	Ponto2D B;

	/**
	 * Cria o segmento ligando (x1,y1) a (x2,y2).
	 * Interação a interação:
	 * 1) Instancia o ponto A
	 * 2) Instancia o ponto B
	 */
	public Linha2D(float x1, float y1, float x2, float y2) {
		// Interação 1: extremo A
		A = new Ponto2D(x1, y1);
		// Interação 2: extremo B
		B = new Ponto2D(x2, y2);
	}

	/**
	 * Translada o segmento inteiro: aplica a mesma translação em A e em B.
	 * Interação: 1) move A  2) move B
	 */
	public void translate(float dx, float dy) {
		// Interação 1
		A.translate(dx, dy);
		// Interação 2
		B.translate(dx, dy);
	}

	/**
	 * Escala o segmento (cada extremo em relação à origem).
	 * Interação: 1) escala A  2) escala B
	 */
	public void scale(float sx, float sy) {
		// Interação 1
		A.scale(sx, sy);
		// Interação 2
		B.scale(sx, sy);
	}

	/**
	 * Rotaciona o segmento em torno da origem (cada extremo).
	 * Para rotacionar em torno de um pivô pC, o MainCanvas faz antes:
	 * translate(-pC) → rotate → translate(+pC).
	 * Interação: 1) rotaciona A  2) rotaciona B
	 */
	public void rotate(float ang) {
		// Interação 1
		A.rotate(ang);
		// Interação 2
		B.rotate(ang);
	}
}
