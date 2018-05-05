package mainpack;


import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;


@RunWith(JUnitPlatform.class)
class Unittest {

	
	/**
	 * Stable Test
	 * 	0 0 0 0
		0 1 1 0
		0 1 1 0
		0 0 0 0
	 */
	@Test
	void stableTest() {
		int xSize = 4;
		int ySize = 4;
		Model m = new Model(xSize,ySize);
		m.incSurroundingPoints(1, 2);
		m.createLivingCell(1, 1);
		m.createLivingCell(2, 1);
		m.createLivingCell(1, 2);
		m.createLivingCell(2, 2);
		boolean [][] firstGen = m.getCurGenerationCopy();
		for (int i = 0; i < 5; i++) {
			m.printCurGen();
			m.setNextGeneration();
		}
		boolean [][] curGen = m.getCurGenerationCopy();
		boolean stable = true;
		for(int y = 0;y<ySize; y++) {
			for(int x = 0;x<xSize; x++) {
				if(curGen[y][x] != firstGen[y][x]) {
					stable = false;
					break;
				}
			}
			if(!stable) {
				break;
			}
		}
		assertTrue(stable);
	}
}
