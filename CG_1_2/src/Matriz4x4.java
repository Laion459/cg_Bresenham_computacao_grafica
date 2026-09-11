/**
 * Matriz 4×4 em coordenadas homogêneas (aula 4 / Foley & Van Dam).
 * <p>
 * Convenção: vetor-coluna {@code P' = M · P}.
 * Armazenamento {@code m[linha][coluna]}.
 */
public class Matriz4x4 {
	float[][] m = new float[4][4];

	public Matriz4x4() {
		identidade();
	}

	public Matriz4x4(float[][] valores) {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				m[i][j] = valores[i][j];
			}
		}
	}

	/** Matriz identidade. */
	public void identidade() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				m[i][j] = (i == j) ? 1f : 0f;
			}
		}
	}

	/**
	 * Multiplica esta matriz por {@code outra}: resultado = this · outra.
	 */
	public Matriz4x4 multiplicar(Matriz4x4 outra) {
		Matriz4x4 r = new Matriz4x4();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				r.m[i][j] = 0f;
				for (int k = 0; k < 4; k++) {
					r.m[i][j] += m[i][k] * outra.m[k][j];
				}
			}
		}
		return r;
	}

	/**
	 * Aplica a matriz a um ponto homogêneo (x,y,z,w) e devolve um novo ponto.
	 */
	public Ponto3D transformar(Ponto3D p) {
		float x = m[0][0] * p.X + m[0][1] * p.Y + m[0][2] * p.Z + m[0][3] * p.W;
		float y = m[1][0] * p.X + m[1][1] * p.Y + m[1][2] * p.Z + m[1][3] * p.W;
		float z = m[2][0] * p.X + m[2][1] * p.Y + m[2][2] * p.Z + m[2][3] * p.W;
		float w = m[3][0] * p.X + m[3][1] * p.Y + m[3][2] * p.Z + m[3][3] * p.W;
		return new Ponto3D(x, y, z, w);
	}

	/** Translação 3D T(dx, dy, dz). */
	public static Matriz4x4 translacao(float dx, float dy, float dz) {
		Matriz4x4 t = new Matriz4x4();
		t.m[0][3] = dx;
		t.m[1][3] = dy;
		t.m[2][3] = dz;
		return t;
	}

	/** Escala 3D S(sx, sy, sz) em relação à origem. */
	public static Matriz4x4 escala(float sx, float sy, float sz) {
		Matriz4x4 s = new Matriz4x4();
		s.m[0][0] = sx;
		s.m[1][1] = sy;
		s.m[2][2] = sz;
		return s;
	}

	/**
	 * Rotação no sentido anti-horário em torno do eixo X (passa pela origem).
	 */
	public static Matriz4x4 rotacaoX(float ang) {
		float c = (float) Math.cos(ang);
		float s = (float) Math.sin(ang);
		Matriz4x4 r = new Matriz4x4();
		r.m[1][1] = c;
		r.m[1][2] = -s;
		r.m[2][1] = s;
		r.m[2][2] = c;
		return r;
	}

	/**
	 * Rotação no sentido anti-horário em torno do eixo Y (passa pela origem).
	 */
	public static Matriz4x4 rotacaoY(float ang) {
		float c = (float) Math.cos(ang);
		float s = (float) Math.sin(ang);
		Matriz4x4 r = new Matriz4x4();
		r.m[0][0] = c;
		r.m[0][2] = s;
		r.m[2][0] = -s;
		r.m[2][2] = c;
		return r;
	}

	/**
	 * Rotação no sentido anti-horário em torno do eixo Z (passa pela origem).
	 */
	public static Matriz4x4 rotacaoZ(float ang) {
		float c = (float) Math.cos(ang);
		float s = (float) Math.sin(ang);
		Matriz4x4 r = new Matriz4x4();
		r.m[0][0] = c;
		r.m[0][1] = -s;
		r.m[1][0] = s;
		r.m[1][1] = c;
		return r;
	}
}
