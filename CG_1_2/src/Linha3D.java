/**
 * Segmento 3D definido por dois {@link Ponto3D}.
 * Transformações se aplicam nos dois extremos (mesma ideia do 2D).
 */
public class Linha3D {
	Ponto3D A;
	Ponto3D B;

	public Linha3D(Ponto3D a, Ponto3D b) {
		A = a;
		B = b;
	}

	public Linha3D(float x1, float y1, float z1, float x2, float y2, float z2) {
		A = new Ponto3D(x1, y1, z1);
		B = new Ponto3D(x2, y2, z2);
	}

	public void transformar(Matriz4x4 matriz) {
		A.transformar(matriz);
		B.transformar(matriz);
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
}
