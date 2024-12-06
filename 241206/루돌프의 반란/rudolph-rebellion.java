

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

public class Main {

	static int N, M, P, C, D, cnt;
	static int ri, rj;
	static int[] points;
	static boolean[] dead;
	static Map<Integer, int[]> santas;
	static int[][] arr;
	static int[] dri = {-1, -1, -1, 0, 1, 1, 1, 0};
	static int[] drj = {-1, 0, 1, 1, 1, 0, -1, -1};
	static int[] dsi = {-1, 0, 1, 0};
	static int[] dsj = {0, 1, 0, -1};
 	public static void main(String[] args) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		StringTokenizer st = new StringTokenizer(br.readLine());
		N = Integer.parseInt(st.nextToken());
		M = Integer.parseInt(st.nextToken());
		P = Integer.parseInt(st.nextToken());
		C = Integer.parseInt(st.nextToken());
		D = Integer.parseInt(st.nextToken());
		points = new int[P + 1];
		santas = new HashMap<Integer, int[]>();
		arr = new int[N][N];
		dead = new boolean[P + 1];
		cnt = P;
		
		st = new StringTokenizer(br.readLine());
		ri = Integer.parseInt(st.nextToken()) - 1;
		rj = Integer.parseInt(st.nextToken()) - 1;
		
		for (int i = 0; i < P; i++) {
			st = new StringTokenizer(br.readLine());
			int num = Integer.parseInt(st.nextToken());
			int sy = Integer.parseInt(st.nextToken()) - 1;
			int sx = Integer.parseInt(st.nextToken()) - 1;
			santas.put(num, new int[] {sy, sx, 0});
			arr[sy][sx] = num;
		}
		
		// 턴
		for (int turn = 1; turn <= M; turn++) {
			// [1] 루돌프의 이동
			// 가장 가까운 산타 찾기, 여러개면 r큰, c큰 순서로 이동
			int mn = 5001; int mxi = -1; int mxj = -1;
			for (int i = santas.size(); i >= 1; i--) {
				if (dead[i]) continue;
				int[] santa = santas.get(i);
				int si = santa[0];
				int sj = santa[1];
				int dist = (ri - si) * (ri - si) + (rj - sj) * (rj - sj);
				if ((dist < mn) || (dist == mn && si > mxi) || (dist == mn && si == mxi && sj > mxj)) {
					mn = dist;
					mxi = si;
					mxj = sj;
					
				}
			}
			
			// 8방향 중 산타와 가까워지는 방향으로 이동하기
			int nrd = -1;
			int mnDist = 5000;
			for (int i = 0; i < 8; i++) {
				int ni = ri + dri[i];
				int nj = rj + drj[i];
				if (ni < 0 || ni >= N || nj < 0 || nj >= N) continue;
				int newDist = (ni - mxi) * (ni - mxi) + (nj - mxj) * (nj - mxj);
				if (newDist < mnDist) {
					nrd = i;
					mnDist = newDist;
				}
			}
			
			// 이동처리
			ri = ri + dri[nrd];
			rj = rj + drj[nrd];
			// 충돌처리
			if (arr[ri][rj] != 0) {
				crash(ri, rj, nrd, C, true, turn);
			}
			
			if (cnt == 0) {
				break;
			}
			
			// [2] 산타의 이동, 충돌, 상호작용
			for (int i = 1; i <= P; i++) {
				if (dead[i]) continue;
				int[] santa = santas.get(i);
				if (santa[2] > turn) continue;
				if (turn == 5 && i == 1) System.out.println("?");
				int si = santa[0];
				int sj = santa[1];
				
				int dist = (ri - si) * (ri - si) + (rj - sj) * (rj - sj);
				int nsd = -1;
				for (int d = 3; d >= 0; d--) {
					int ni = si + dsi[d];
					int nj = sj + dsj[d];
					if (ni < 0 || ni >= N || nj < 0 || nj >= N || arr[ni][nj] != 0) continue;
					int newDist = (ri - ni) * (ri - ni) + (rj - nj) * (rj - nj);
					if (newDist <= dist) {
						nsd = d;
						dist = newDist;
					}
				}
				
				if (nsd != -1) {
					int ni = si + dsi[nsd];
					int nj = sj + dsj[nsd];
					arr[si][sj] = 0;
					arr[ni][nj] = i;
					santa[0] = ni;
					santa[1] = nj;
					if (ni == ri && nj == rj) {
						crash(ni, nj, nsd, D, false, turn);
					}
				} else {
					continue;
				}
				
			}
			
			if (cnt == 0) {
				break;
			}
			for (int i = 1; i <= P; i++) {
				if (dead[i]) continue;
				System.out.println(turn + " | " + points[1]);
				points[i] += 1;
			}
			
		} // turn
		for (int i = 1; i <= P; i++) {
			System.out.print(points[i] + " ");
		}
	} // main
 	
 	// 충돌 처리, 상호 작용 처리
 	// 산타 이동 시키기, arr 수정, santas 수정
	private static void crash(int i, int j, int dir, int po, boolean isR, int turn) {
		// C = 루 -> 산
		// D = 산 -> 루
		int num = arr[i][j];
		arr[i][j] = 0;
		
		int ni = -1; int nj = -1;
		if (!isR) {
			dir = (dir ^ 2);
			ni = i + po*dsi[dir];
			nj = j + po*dsj[dir];
		} else {
			ni = i + po*dri[dir];
			nj = j + po*drj[dir];
		}
		points[num] += po;
		
		if (ni < 0 || ni >= N || nj < 0 || nj >= N) {
			dead[num] = true;
			cnt--;
			return;
		}
		
		// ni, nj에 다른 산타 있다면 상호작용
		if (arr[ni][nj] != 0) {
			interact(ni, nj, isR, dir);
		}
		arr[ni][nj] = num;
		int[] santa = santas.get(num);
		santa[0] = ni;
		santa[1] = nj;
		santa[2] = turn + 2; // 기절처리
		
	}
	
	// 상호작용
	// arr[i, j] 비우고 다음칸으로 옮기고 다음칸에 있으면 재귀적으로 이동
	private static void interact(int i, int j, boolean isR, int dir) {
		int num = arr[i][j];
		arr[i][j] = 0;
		
		int ni = -1; int nj = -1;
		if (!isR) {
			ni = i + dsi[dir];
			nj = j + dsj[dir];
		} else {
			ni = i + dri[dir];
			nj = j + drj[dir];
		}
		
		if (ni < 0 || ni >= N || nj < 0 || nj >= N) {
			dead[num] = true;
			cnt--;
			return;
		}
		
		if (arr[ni][nj] != 0) {
			interact(ni, nj, isR, dir);
		}
		arr[ni][nj] = num;
		int[] santa = santas.get(num);
		santa[0] = ni;
		santa[1] = nj;
	}
} // class
