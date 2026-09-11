/**
 * Ponto 3D em coordenadas homogêneas: vetor 4D (x, y, z, w), com w = 1.
 * <p>
 * Transformações afins usam {@link Matriz4x4} (aula 4).
 */
public class Ponto3D {
	float X;
	float Y;
	float Z;
	float W;

	public Ponto3D(float x, float y, float z) {
		this(x, y, z, 1f);
	}

	public Ponto3D(float x, float y, float z, float w) {
		X = x;
		Y = y;
		Z = z;
		W = w;
	}

	/**
	 * Aplica a matriz 4×4 no próprio ponto (efeito colateral),
	 * equivalente a {@code P = M · P}.
	 */
	public void transformar(Matriz4x4 matriz) {
		Ponto3D p = matriz.transformar(this);
		X = p.X;
		Y = p.Y;
		Z = p.Z;
		W = p.W;
		if (W != 0f && W != 1f) {
			X /= W;
			Y /= W;
			Z /= W;
			W = 1f;
		}
	}

	public void translate(float dx, float dy, float dz) {
		transformar(Matriz4x4.translacao(dx, dy, dz));
	}

	public void scale(float sx, float sy, float sz) {
		transformar(Matriz4x4.escala(sx, sy, sz));
	}

	public void rotateX(float ang) {
		transformar(Matriz4x4.rotacaoX(ang));
	}

	public void rotateY(float ang) {
		transformar(Matriz4x4.rotacaoY(ang));
	}

	public void rotateZ(float ang) {
		transformar(Matriz4x4.rotacaoZ(ang));
	}

	/**
	 * Projeção ortográfica + deslocamento para o centro da tela.
	 * Y do mundo cresce para cima; na tela Y cresce para baixo.
	 *
	 * @param centroX centro horizontal da tela
	 * @param centroY centro vertical da tela
	 * @return {sx, sy} em pixels inteiros
	 */
	public int[] projetarOrtografica(int centroX, int centroY) {
		int sx = Math.round(centroX + X);
		int sy = Math.round(centroY - Y);
		return new int[] { sx, sy };
	}

	/**
	 * Projeção perspectiva simples (câmera olhando -Z, foco em {@code distancia}).
	 * Útil para sentir profundidade ao girar em X/Y.
	 */
	public int[] projetarPerspectiva(int centroX, int centroY, float distancia) {
		float zCam = Z + distancia;
		if (zCam < 0.1f) {
			zCam = 0.1f;
		}
		float f = distancia / zCam;
		int sx = Math.round(centroX + X * f);
		int sy = Math.round(centroY - Y * f);
		return new int[] { sx, sy };
	}
}
