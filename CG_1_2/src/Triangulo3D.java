/**
 * Triângulo 3D = três vértices não colineares (slide “Transformando Planos”).
 * Cada aresta é um {@link Linha3D}; a mesma matriz 4×4 vai nos três vértices.
 */
public class Triangulo3D {
	Ponto3D A;
	Ponto3D B;
	Ponto3D C;

	public Triangulo3D(Ponto3D a, Ponto3D b, Ponto3D c) {
		A = a;
		B = b;
		C = c;
	}

	public Triangulo3D(
			float ax, float ay, float az,
			float bx, float by, float bz,
			float cx, float cy, float cz) {
		A = new Ponto3D(ax, ay, az);
		B = new Ponto3D(bx, by, bz);
		C = new Ponto3D(cx, cy, cz);
	}

	/** Aplica a mesma transformação afim nos três vértices. */
	public void transformar(Matriz4x4 matriz) {
		A.transformar(matriz);
		B.transformar(matriz);
		C.transformar(matriz);
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

	/** Três arestas A→B, B→C, C→A. */
	public Linha3D[] arestas() {
		return new Linha3D[] {
				new Linha3D(A, B),
				new Linha3D(B, C),
				new Linha3D(C, A)
		};
	}
}
