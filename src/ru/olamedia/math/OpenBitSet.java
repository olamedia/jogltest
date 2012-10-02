package ru.olamedia.math;

import java.io.Serializable;
import java.util.Arrays;

/**
 * http://www.apache.org/licenses/LICENSE-2.0 (Lucene)
 * 
 * An "open" BitSet implementation that allows direct access to the
 * array of words
 * storing the bits.
 * <p/>
 * Unlike java.util.bitet, the fact that bits are packed into an array of longs
 * is part of the interface. This allows efficient implementation of other
 * algorithms by someone other than the author. It also allows one to
 * efficiently implement alternate serialization or interchange formats.
 * <p/>
 * <code>OpenBitSet</code> is faster than <code>java.util.BitSet</code> in most
 * operations and *much* faster at calculating cardinality of sets and results
 * of set operations. It can also handle sets of larger cardinality (up to 64 *
 * 2**32-1)
 * <p/>
 * The goals of <code>OpenBitSet</code> are the fastest implementation possible,
 * and maximum code reuse. Extra safety and encapsulation may always be built on
 * top, but if that's built in, the cost can never be removed (and hence people
 * re-implement their own version in order to get better performance). If you
 * want a "safe", totally encapsulated (and slower and limited) BitSet class,
 * use <code>java.util.BitSet</code>.
 * <p/>
 * <h3>Performance Results</h3>
 * 
 * Test system: Pentium 4, Sun Java 1.5_06 -server -Xbatch -Xmx64M <br/>
 * BitSet size = 1,000,000 <br/>
 * Results are java.util.BitSet time divided by OpenBitSet time.
 * <table border="1">
 * <tr>
 * <th></th>
 * <th>cardinality</th>
 * <th>intersect_count</th>
 * <th>union</th>
 * <th>nextSetBit</th>
 * <th>get</th>
 * <th>iterator</th>
 * </tr>
 * <tr>
 * <th>50% full</th>
 * <td>3.36</td>
 * <td>3.96</td>
 * <td>1.44</td>
 * <td>1.46</td>
 * <td>1.99</td>
 * <td>1.58</td>
 * </tr>
 * <tr>
 * <th>1% full</th>
 * <td>3.31</td>
 * <td>3.90</td>
 * <td>&nbsp;</td>
 * <td>1.04</td>
 * <td>&nbsp;</td>
 * <td>0.99</td>
 * </tr>
 * </table>
 * 
 * @author olamedia (modified version)
 * 
 * @author yonik
 * @version $Id$
 */

public class OpenBitSet implements Cloneable, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7041505168827686846L;
	public long[] bits;
	public int wlen; // number of words (elements) used in the array

	public OpenBitSet() {

	}

	public OpenBitSet(long numBits) {
		bits = new long[(int) (((numBits - 1) >>> 6) + 1)];
		wlen = bits.length;
	}

	public OpenBitSet(long[] bits, int numWords) {
		this.bits = bits;
		this.wlen = numWords;
	}

	public long[] getBits() {
		return bits;
	}

	public void setBits(long[] bits) {
		this.bits = bits;
	}

	public int getNumWords() {
		return wlen;
	}

	public void setNumWords(int nWords) {
		this.wlen = nWords;
	}

	public static int wordNum(int index) {
		// java doesn't currently accept 64 bit array indicies, so returning
		// a long isn't needed.
		return index >> 6; // div 64
	}

	public static int wordNum(long index) {
		// java doesn't currently accept 64 bit array indicies, so returning
		// a long isn't needed.
		return (int) (index >> 6); // div 64
	}

	public static int bitNum(int index) {
		// should and or cast come first for best efficiency?
		return index & 0x0000003f; // mod 64
	}

	public static int bitNum(long index) {
		// should and or cast come first for best efficiency?
		return (int) index & 0x0000003f; // mod 64
	}

	public static long bitMask(int index) {
		return 1L << (index & 0x0000003f);
	}

	/** Returns true or false for the specified bit index */
	public boolean get(int index) {
		int i = index >> 6; // div 64
		// signed shift will keep a negative index and force an exception,
		// removing the need for an explicit check.
		int bit = index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		return (bits[i] & bitmask) != 0;
	}

	/** Returns true or false for the specified bit index */
	public boolean get(long index) {
		int i = (int) (index >> 6); // div 64
		// if (i>=bits.length) return false;
		int bit = (int) index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		return (bits[i] & bitmask) != 0;
	}

	// alternate implementation of get()
	public boolean get1(int index) {
		int i = index >> 6; // div 64
		int bit = index & 0x3f; // mod 64
		return ((bits[i] >>> bit) & 0x01) != 0;
		// this does a long shift and a bittest (on x86) vs
		// a long shift, and a long AND, (the test for zero is prob a no-op)
		// testing on a P4 indicates this is slower than (bits[i] & bitmask) !=
		// 0;
	}

	/** returns 1 if the bit is set, 0 if not */
	public int getBit(int index) {
		int i = index >> 6; // div 64
		int bit = index & 0x3f; // mod 64
		return ((int) (bits[i] >>> bit)) & 0x01;
	}

	/***
	 * public boolean get2(int index) {
	 * int word = index >> 6; // div 64
	 * int bit = index & 0x0000003f; // mod 64
	 * return (bits[word] << bit) < 0; // hmmm, this would work if bit
	 * order were reversed
	 * // we could right shift and check for parity bit, if it was available to
	 * us.
	 * }
	 ***/

	public void set(int index) {
		int wordNum = index >> 6; // div 64
		int bit = index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		bits[wordNum] |= bitmask;
	}

	public void set(long index) {
		int wordNum = (int) (index >> 6); // div 64
		int bit = (int) index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		bits[wordNum] |= bitmask;
	}

	public void clear(int index) {
		int wordNum = index >> 6; // div 64
		int bit = index & 0x0000003f; // mod 64
		long bitmask = 1L << bit;
		bits[wordNum] &= ~bitmask;
		// hmmm, it takes one more instruction to clear than it does to set...
		// any
		// way to work around this? If there were only 63 bits per word, we
		// could
		// use a right shift of 10111111...111 in binary to position the 0 in
		// the
		// correct place (using sign extension).
		// Could also use Long.rotateRight() or rotateLeft() *if* they were
		// converted
		// by the JVM into a native instruction.
	}

	public void clear(long index) {
		int wordNum = (int) (index >> 6); // div 64
		int bit = (int) index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		bits[wordNum] &= ~bitmask;
	}

	/***
	 * public void clear2(int index) {
	 * int word = index >> 6; // div 64
	 * int bit = index & 0x0000003f; // mod 64
	 * bits[word] &= Long.rotateLeft(0xfffffffe,bit);
	 * }
	 ***/

	public boolean getAndSet(int index) {
		int wordNum = index >> 6; // div 64
		int bit = index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		boolean val = (bits[wordNum] & bitmask) != 0;
		bits[wordNum] |= bitmask;
		return val;
	}

	public boolean getAndSet(long index) {
		int wordNum = (int) (index >> 6); // div 64
		int bit = (int) index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		boolean val = (bits[wordNum] & bitmask) != 0;
		bits[wordNum] |= bitmask;
		return val;
	}

	/** flips a bit */
	public void flip(int index) {
		int wordNum = index >> 6; // div 64
		int bit = index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		bits[wordNum] ^= bitmask;
	}

	/** flips a bit */
	public void flip(long index) {
		int wordNum = (int) (index >> 6); // div 64
		int bit = (int) index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		bits[wordNum] ^= bitmask;
	}

	/** flips a bit and returns the resulting bit value */
	public boolean flipAndGet(int index) {
		int wordNum = index >> 6; // div 64
		int bit = index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		bits[wordNum] ^= bitmask;
		return (bits[wordNum] & bitmask) != 0;
	}

	/** flips a bit and returns the resulting bit value */
	public boolean flipAndGet(long index) {
		int wordNum = (int) (index >> 6); // div 64
		int bit = (int) index & 0x3f; // mod 64
		long bitmask = 1L << bit;
		bits[wordNum] ^= bitmask;
		return (bits[wordNum] & bitmask) != 0;
	}

	static long pop_array5(long A[], int wordOffset, int numWords) {
		int n = numWords;
		long tot = 0;
		long ones = 0, twos = 0, twosA, twosB, fours = 0, foursA, foursB, eights;

		int i;
		for (i = wordOffset; i <= n - 8; i += 8) {
			/***
			 * #define CSA(h,l, a,b,c) \
			 * {unsigned u = a ^ b; unsigned v = c; \
			 * h = (a & b) | (u & v); l = u ^ v;}
			 ***/

			// CSA(twosA, ones, ones, A[i], A[i+1])
			{
				long a = ones, b = A[i], c = A[i + 1];
				long u = a ^ b, v = c;
				long h = (a & b) | (u & v), l = u ^ v;
				twosA = h;
				ones = l;
			}

			// CSA(twosB, ones, ones, A[i+2], A[i+3])
			{
				long a = ones, b = A[i + 2], c = A[i + 3];
				long u = a ^ b, v = c;
				long h = (a & b) | (u & v), l = u ^ v;
				twosB = h;
				ones = l;
			}

			// CSA(foursA, twos, twos, twosA, twosB)
			{
				long a = twos, b = twosA, c = twosB;
				long u = a ^ b, v = c;
				long h = (a & b) | (u & v), l = u ^ v;
				foursA = h;
				twos = l;
			}

			// CSA(twosA, ones, ones, A[i+4], A[i+5])
			{
				long a = ones, b = A[i + 4], c = A[i + 5];
				long u = a ^ b, v = c;
				long h = (a & b) | (u & v), l = u ^ v;
				twosA = h;
				ones = l;
			}

			// CSA(twosB, ones, ones, A[i+6], A[i+7])
			{
				long a = ones, b = A[i + 6], c = A[i + 7];
				long u = a ^ b, v = c;
				long h = (a & b) | (u & v), l = u ^ v;
				twosB = h;
				ones = l;
			}

			// CSA(foursB, twos, twos, twosA, twosB)
			{
				long a = twos, b = twosA, c = twosB;
				long u = a ^ b, v = c;
				long h = (a & b) | (u & v), l = u ^ v;
				foursB = h;
				twos = l;
			}

			// CSA(eights, fours, fours, foursA, foursB)
			// CSA(foursB, twos, twos, twosA, twosB)
			{
				long a = fours, b = foursA, c = foursB;
				long u = a ^ b, v = c;
				long h = (a & b) | (u & v), l = u ^ v;
				eights = h;
				fours = l;
			}

			tot += pop(eights);
		}

		tot = (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot << 3);

		for (i = i; i < n; i++)
			// Add in the last elements
			tot = tot + pop(A[i]);

		return tot;
	}

	/**
	 * Returns the number of bits set in the long
	 */
	public static int pop(long x) {
		/***
		 * Hacker's Delight 32 bit pop function:
		 * http://www.hackersdelight.org/HDcode/newCode/pop_arrayHS.cc
		 * 
		 * int pop(unsigned x) {
		 * x = x - ((x >> 1) & 0x55555555);
		 * x = (x & 0x33333333) + ((x >> 2) & 0x33333333);
		 * x = (x + (x >> 4)) & 0x0F0F0F0F;
		 * x = x + (x >> 8);
		 * x = x + (x >> 16);
		 * return x & 0x0000003F;
		 * }
		 ***/

		// 64 bit extension of the C function from above
		x = x - ((x >>> 1) & 0x5555555555555555L);
		x = (x & 0x3333333333333333L) + ((x >>> 2) & 0x3333333333333333L);
		x = (x + (x >>> 4)) & 0x0F0F0F0F0F0F0F0FL;
		x = x + (x >>> 8);
		x = x + (x >>> 16);
		x = x + (x >>> 32);
		return ((int) x) & 0x7F;
	}

	/***
	 * http://supertech.lcs.mit.edu/~heinz/dt/node7.html Ernst A. Heinz
	 * DARKTHOUGHT prefers the following non-iterative formulation that
	 * stems from the well-known ``Hacker's Memory'' collection of
	 * programming tricks. It performs better than intuitive methods with
	 * lookup tables because the tables get either too large or need too many
	 * lookups.1.3
	 * 
	 * #define m1 ((unsigned_64) 0x5555555555555555)
	 * #define m2 ((unsigned_64) 0x3333333333333333)
	 * 
	 * unsigned int non_iterative_pop(const unsigned_64 b) {
	 * unsigned_32 n;
	 * const unsigned_64 a = b - ((b >> 1) & m1);
	 * const unsigned_64 c = (a & m2) + ((a >> 2) & m2);
	 * n = ((unsigned_32) c) + ((unsigned_32) (c >> 32));
	 * n = (n & 0x0F0F0F0F) + ((n >> 4) & 0x0F0F0F0F);
	 * n = (n & 0xFFFF) + (n >> 16);
	 * n = (n & 0xFF) + (n >> 8);
	 * return n;
	 * }
	 * 
	 * // Looks like 19 simple arithmetic operations -YCS
	 ***/

	public static int pop(long v0, long v1, long v2, long v3) {
		// derived from pop_array by setting last four elems to 0.
		// exchanges one pop() call for 10 elementary operations
		// saving about 7 instructions... is there a better way?
		long twosA = v0 & v1;
		long ones = v0 ^ v1;

		long u2 = ones ^ v2;
		long twosB = (ones & v2) | (u2 & v3);
		ones = u2 ^ v3;

		long fours = (twosA & twosB);
		long twos = twosA ^ twosB;

		return (pop(fours) << 2) + (pop(twos) << 1) + pop(ones);

	}

	/***
	 * Counts the number of set bits in an array of longs
	 */
	public static long pop_array(long A[], int wordOffset, int numWords) {
		/*
		 * Robert Harley and David Seal's bit counting algorithm, as documented
		 * in the revisions of Hacker's Delight
		 * http://www.hackersdelight.org/revisions.pdf
		 * http://www.hackersdelight.org/HDcode/newCode/pop_arrayHS.cc
		 * 
		 * This function was adapted to Java, and extended to use 64 bit words.
		 * if only we had access to wider registers like SSE from java...
		 * 
		 * This function can be transformed to compute the popcoun of other
		 * functions
		 * on bitsets via something like this:
		 * sed 's/A\[\([^]]*\)\]/\(A[\1] \& B[\1]\)/g'
		 */
		int n = wordOffset + numWords;
		long tot = 0, tot8 = 0;
		long ones = 0, twos = 0, fours = 0;

		int i;
		for (i = wordOffset; i <= n - 8; i += 8) {
			/***
			 * C macro from Hacker's Delight
			 * #define CSA(h,l, a,b,c) \
			 * {unsigned u = a ^ b; unsigned v = c; \
			 * h = (a & b) | (u & v); l = u ^ v;}
			 ***/

			long twosA, twosB, foursA, foursB, eights;

			// CSA(twosA, ones, ones, A[i], A[i+1])
			{
				long b = A[i], c = A[i + 1];
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, A[i+2], A[i+3])
			{
				long b = A[i + 2], c = A[i + 3];
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursA, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			// CSA(twosA, ones, ones, A[i+4], A[i+5])
			{
				long b = A[i + 4], c = A[i + 5];
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, A[i+6], A[i+7])
			{
				long b = A[i + 6], c = A[i + 7];
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursB, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursB = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}

			// CSA(eights, fours, fours, foursA, foursB)
			{
				long u = fours ^ foursA;
				eights = (fours & foursA) | (u & foursB);
				fours = u ^ foursB;
			}
			tot8 += pop(eights);
		}

		if (i <= n - 4) {
			long twosA, twosB, foursA, eights;
			{
				long b = A[i], c = A[i + 1];
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long b = A[i + 2], c = A[i + 3];
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 4;
		}

		if (i <= n - 2) {
			long b = A[i], c = A[i + 1];
			long u = ones ^ b;
			long twosA = (ones & b) | (u & c);
			ones = u ^ c;

			long foursA = twos & twosA;
			twos = twos ^ twosA;

			long eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 2;
		}

		if (i < n) {
			tot += pop(A[i]);
		}

		tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);

		return tot;
	}

	/**
	 * Returns the popcount or cardinality of the two sets after an
	 * intersection.
	 * Neither array is modified.
	 */
	public static long pop_intersect(long A[], long B[], int wordOffset, int numWords) {
		// generated from pop_array via sed 's/A\[\([^]]*\)\]/\(A[\1] \&
		// B[\1]\)/g'
		int n = wordOffset + numWords;
		long tot = 0, tot8 = 0;
		long ones = 0, twos = 0, fours = 0;

		int i;
		for (i = wordOffset; i <= n - 8; i += 8) {
			long twosA, twosB, foursA, foursB, eights;

			// CSA(twosA, ones, ones, (A[i] & B[i]), (A[i+1] & B[i+1]))
			{
				long b = (A[i] & B[i]), c = (A[i + 1] & B[i + 1]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, (A[i+2] & B[i+2]), (A[i+3] & B[i+3]))
			{
				long b = (A[i + 2] & B[i + 2]), c = (A[i + 3] & B[i + 3]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursA, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			// CSA(twosA, ones, ones, (A[i+4] & B[i+4]), (A[i+5] & B[i+5]))
			{
				long b = (A[i + 4] & B[i + 4]), c = (A[i + 5] & B[i + 5]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, (A[i+6] & B[i+6]), (A[i+7] & B[i+7]))
			{
				long b = (A[i + 6] & B[i + 6]), c = (A[i + 7] & B[i + 7]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursB, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursB = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}

			// CSA(eights, fours, fours, foursA, foursB)
			{
				long u = fours ^ foursA;
				eights = (fours & foursA) | (u & foursB);
				fours = u ^ foursB;
			}
			tot8 += pop(eights);
		}

		if (i <= n - 4) {
			long twosA, twosB, foursA, eights;
			{
				long b = (A[i] & B[i]), c = (A[i + 1] & B[i + 1]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long b = (A[i + 2] & B[i + 2]), c = (A[i + 3] & B[i + 3]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 4;
		}

		if (i <= n - 2) {
			long b = (A[i] & B[i]), c = (A[i + 1] & B[i + 1]);
			long u = ones ^ b;
			long twosA = (ones & b) | (u & c);
			ones = u ^ c;

			long foursA = twos & twosA;
			twos = twos ^ twosA;

			long eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 2;
		}

		if (i < n) {
			tot += pop((A[i] & B[i]));
		}

		tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);

		return tot;
	}

	/**
	 * Returns the popcount or cardinality of the union of two sets.
	 * Neither array is modified.
	 */
	public static long pop_union(long A[], long B[], int wordOffset, int numWords) {
		// generated from pop_array via sed 's/A\[\([^]]*\)\]/\(A[\1] \|
		// B[\1]\)/g'
		int n = wordOffset + numWords;
		long tot = 0, tot8 = 0;
		long ones = 0, twos = 0, fours = 0;

		int i;
		for (i = wordOffset; i <= n - 8; i += 8) {
			/***
			 * C macro from Hacker's Delight
			 * #define CSA(h,l, a,b,c) \
			 * {unsigned u = a ^ b; unsigned v = c; \
			 * h = (a & b) | (u & v); l = u ^ v;}
			 ***/

			long twosA, twosB, foursA, foursB, eights;

			// CSA(twosA, ones, ones, (A[i] | B[i]), (A[i+1] | B[i+1]))
			{
				long b = (A[i] | B[i]), c = (A[i + 1] | B[i + 1]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, (A[i+2] | B[i+2]), (A[i+3] | B[i+3]))
			{
				long b = (A[i + 2] | B[i + 2]), c = (A[i + 3] | B[i + 3]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursA, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			// CSA(twosA, ones, ones, (A[i+4] | B[i+4]), (A[i+5] | B[i+5]))
			{
				long b = (A[i + 4] | B[i + 4]), c = (A[i + 5] | B[i + 5]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, (A[i+6] | B[i+6]), (A[i+7] | B[i+7]))
			{
				long b = (A[i + 6] | B[i + 6]), c = (A[i + 7] | B[i + 7]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursB, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursB = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}

			// CSA(eights, fours, fours, foursA, foursB)
			{
				long u = fours ^ foursA;
				eights = (fours & foursA) | (u & foursB);
				fours = u ^ foursB;
			}
			tot8 += pop(eights);
		}

		if (i <= n - 4) {
			long twosA, twosB, foursA, eights;
			{
				long b = (A[i] | B[i]), c = (A[i + 1] | B[i + 1]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long b = (A[i + 2] | B[i + 2]), c = (A[i + 3] | B[i + 3]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 4;
		}

		if (i <= n - 2) {
			long b = (A[i] | B[i]), c = (A[i + 1] | B[i + 1]);
			long u = ones ^ b;
			long twosA = (ones & b) | (u & c);
			ones = u ^ c;

			long foursA = twos & twosA;
			twos = twos ^ twosA;

			long eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 2;
		}

		if (i < n) {
			tot += pop((A[i] | B[i]));
		}

		tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);

		return tot;
	}

	/**
	 * Returns the popcount or cardinality of A & ~B
	 * Neither array is modified.
	 */
	public static long pop_andnot(long A[], long B[], int wordOffset, int numWords) {
		// generated from pop_array via sed 's/A\[\([^]]*\)\]/\(A[\1] \&
		// ~B[\1]\)/g'
		int n = wordOffset + numWords;
		long tot = 0, tot8 = 0;
		long ones = 0, twos = 0, fours = 0;

		int i;
		for (i = wordOffset; i <= n - 8; i += 8) {
			/***
			 * C macro from Hacker's Delight
			 * #define CSA(h,l, a,b,c) \
			 * {unsigned u = a ^ b; unsigned v = c; \
			 * h = (a & b) | (u & v); l = u ^ v;}
			 ***/

			long twosA, twosB, foursA, foursB, eights;

			// CSA(twosA, ones, ones, (A[i] & ~B[i]), (A[i+1] & ~B[i+1]))
			{
				long b = (A[i] & ~B[i]), c = (A[i + 1] & ~B[i + 1]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, (A[i+2] & ~B[i+2]), (A[i+3] & ~B[i+3]))
			{
				long b = (A[i + 2] & ~B[i + 2]), c = (A[i + 3] & ~B[i + 3]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursA, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			// CSA(twosA, ones, ones, (A[i+4] & ~B[i+4]), (A[i+5] & ~B[i+5]))
			{
				long b = (A[i + 4] & ~B[i + 4]), c = (A[i + 5] & ~B[i + 5]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, (A[i+6] & ~B[i+6]), (A[i+7] & ~B[i+7]))
			{
				long b = (A[i + 6] & ~B[i + 6]), c = (A[i + 7] & ~B[i + 7]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursB, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursB = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}

			// CSA(eights, fours, fours, foursA, foursB)
			{
				long u = fours ^ foursA;
				eights = (fours & foursA) | (u & foursB);
				fours = u ^ foursB;
			}
			tot8 += pop(eights);
		}

		if (i <= n - 4) {
			long twosA, twosB, foursA, eights;
			{
				long b = (A[i] & ~B[i]), c = (A[i + 1] & ~B[i + 1]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long b = (A[i + 2] & ~B[i + 2]), c = (A[i + 3] & ~B[i + 3]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 4;
		}

		if (i <= n - 2) {
			long b = (A[i] & ~B[i]), c = (A[i + 1] & ~B[i + 1]);
			long u = ones ^ b;
			long twosA = (ones & b) | (u & c);
			ones = u ^ c;

			long foursA = twos & twosA;
			twos = twos ^ twosA;

			long eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 2;
		}

		if (i < n) {
			tot += pop((A[i] & ~B[i]));
		}

		tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);

		return tot;
	}

	public static long pop_xor(long A[], long B[], int wordOffset, int numWords) {
		int n = wordOffset + numWords;
		long tot = 0, tot8 = 0;
		long ones = 0, twos = 0, fours = 0;

		int i;
		for (i = wordOffset; i <= n - 8; i += 8) {
			/***
			 * C macro from Hacker's Delight
			 * #define CSA(h,l, a,b,c) \
			 * {unsigned u = a ^ b; unsigned v = c; \
			 * h = (a & b) | (u & v); l = u ^ v;}
			 ***/

			long twosA, twosB, foursA, foursB, eights;

			// CSA(twosA, ones, ones, (A[i] ^ B[i]), (A[i+1] ^ B[i+1]))
			{
				long b = (A[i] ^ B[i]), c = (A[i + 1] ^ B[i + 1]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, (A[i+2] ^ B[i+2]), (A[i+3] ^ B[i+3]))
			{
				long b = (A[i + 2] ^ B[i + 2]), c = (A[i + 3] ^ B[i + 3]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursA, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			// CSA(twosA, ones, ones, (A[i+4] ^ B[i+4]), (A[i+5] ^ B[i+5]))
			{
				long b = (A[i + 4] ^ B[i + 4]), c = (A[i + 5] ^ B[i + 5]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(twosB, ones, ones, (A[i+6] ^ B[i+6]), (A[i+7] ^ B[i+7]))
			{
				long b = (A[i + 6] ^ B[i + 6]), c = (A[i + 7] ^ B[i + 7]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			// CSA(foursB, twos, twos, twosA, twosB)
			{
				long u = twos ^ twosA;
				foursB = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}

			// CSA(eights, fours, fours, foursA, foursB)
			{
				long u = fours ^ foursA;
				eights = (fours & foursA) | (u & foursB);
				fours = u ^ foursB;
			}
			tot8 += pop(eights);
		}

		if (i <= n - 4) {
			long twosA, twosB, foursA, eights;
			{
				long b = (A[i] ^ B[i]), c = (A[i + 1] ^ B[i + 1]);
				long u = ones ^ b;
				twosA = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long b = (A[i + 2] ^ B[i + 2]), c = (A[i + 3] ^ B[i + 3]);
				long u = ones ^ b;
				twosB = (ones & b) | (u & c);
				ones = u ^ c;
			}
			{
				long u = twos ^ twosA;
				foursA = (twos & twosA) | (u & twosB);
				twos = u ^ twosB;
			}
			eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 4;
		}

		if (i <= n - 2) {
			long b = (A[i] ^ B[i]), c = (A[i + 1] ^ B[i + 1]);
			long u = ones ^ b;
			long twosA = (ones & b) | (u & c);
			ones = u ^ c;

			long foursA = twos & twosA;
			twos = twos ^ twosA;

			long eights = fours & foursA;
			fours = fours ^ foursA;

			tot8 += pop(eights);
			i += 2;
		}

		if (i < n) {
			tot += pop((A[i] ^ B[i]));
		}

		tot += (pop(fours) << 2) + (pop(twos) << 1) + pop(ones) + (tot8 << 3);

		return tot;
	}

	public long cardinality() {
		return pop_array(bits, 0, wlen);
	}

	/**
	 * Returns the popcount or cardinality of the intersection of the two sets.
	 * Neither set is modified.
	 */
	public static long intersectionCount(OpenBitSet a, OpenBitSet b) {
		return pop_intersect(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
	}

	/**
	 * Returns the popcount or cardinality of the union of the two sets.
	 * Neither set is modified.
	 */
	public static long unionCount(OpenBitSet a, OpenBitSet b) {
		long tot = pop_union(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
		if (a.wlen < b.wlen) {
			tot += pop_array(b.bits, a.wlen, b.wlen - a.wlen);
		} else if (a.wlen > b.wlen) {
			tot += pop_array(a.bits, b.wlen, a.wlen - b.wlen);
		}
		return tot;
	}

	/**
	 * Returns the popcount or cardinality of "a and not b"
	 * or "intersection(a, not(b))"
	 * Neither set is modified.
	 */
	public static long andNotCount(OpenBitSet a, OpenBitSet b) {
		long tot = pop_andnot(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
		if (a.wlen > b.wlen) {
			tot += pop_array(a.bits, b.wlen, a.wlen - b.wlen);
		}
		return tot;
	}

	/**
	 * Returns the popcount or cardinality of the exclusive-or of the two sets.
	 * Neither set is modified.
	 */
	public static long xorCount(OpenBitSet a, OpenBitSet b) {
		long tot = pop_xor(a.bits, b.bits, 0, Math.min(a.wlen, b.wlen));
		if (a.wlen < b.wlen) {
			tot += pop_array(b.bits, a.wlen, b.wlen - a.wlen);
		} else if (a.wlen > b.wlen) {
			tot += pop_array(a.bits, b.wlen, a.wlen - b.wlen);
		}
		return tot;
	}

	/**
	 * Returns the index of the first set bit starting at the index specified.
	 * -1 is returned if there are no more set bits.
	 */
	public int nextSetBit(int index) {
		int i = index >> 6;
		if (i >= wlen)
			return -1;
		int subIndex = index & 0x3f; // index within the word
		long word = bits[i] >> subIndex; // skip all the bits to the right of
											// index

		if (word != 0) {
			return (i << 6) + subIndex + ntz(word);
		}

		while (++i < wlen) {
			word = bits[i];
			if (word != 0)
				return (i << 6) + ntz(word);
		}

		return -1;
	}

	/**
	 * Returns the index of the first set bit starting at the index specified.
	 * -1 is returned if there are no more set bits.
	 */
	public long nextSetBit(long index) {
		int i = (int) (index >>> 6);
		if (i >= wlen)
			return -1;
		int subIndex = (int) index & 0x3f; // index within the word
		long word = bits[i] >>> subIndex; // skip all the bits to the right of
											// index

		if (word != 0) {
			return (((long) i) << 6) + (subIndex + ntz(word));
		}

		while (++i < wlen) {
			word = bits[i];
			if (word != 0)
				return (((long) i) << 6) + ntz(word);
		}

		return -1;
	}

	/***
	 * python code to generate ntzTable
	 * def ntz(val):
	 * if val==0: return 8
	 * i=0
	 * while (val&0x01)==0:
	 * i = i+1
	 * val >>= 1
	 * return i
	 * print ','.join([ str(ntz(i)) for i in range(256) ])
	 ***/
	public static final byte ntzTable[] = { 8, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3,
			0, 1, 0, 2, 0, 1, 0, 5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2,
			0, 1, 0, 6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5,
			0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 7, 0, 1, 0, 2,
			0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5, 0, 1, 0, 2, 0, 1, 0, 3,
			0, 1, 0, 2, 0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 6, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2,
			0, 1, 0, 4, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 5, 0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0, 4,
			0, 1, 0, 2, 0, 1, 0, 3, 0, 1, 0, 2, 0, 1, 0 };

	/** Returns number of trailing zeros in the 64 bit long value */
	public static int ntz(long val) {
		// A full binary search to determine the low byte was slower than
		// a linear search for nextSetBit(). This is most likely because
		// the implementation of nextSetBit() shifts bits to the right,
		// increasing
		// the probability that the first non-zero byte is in the rhs.
		//
		// This implementation does a single binary search at the top level only
		// so that all other bit shifting can be done on ints instead of longs
		// to
		// remain friendly to 32 bit architectures. In addition, the case of a
		// non-zero first byte is checked for first because it is the most
		// common
		// in dense bit arrays.

		int lower = (int) val;
		int lowByte = lower & 0xff;
		if (lowByte != 0)
			return ntzTable[lowByte];

		if (lower != 0) {
			lowByte = (lower >>> 8) & 0xff;
			if (lowByte != 0)
				return ntzTable[lowByte] + 8;
			lowByte = (lower >>> 16) & 0xff;
			if (lowByte != 0)
				return ntzTable[lowByte] + 16;
			// no need to mask off low byte for the last byte in the 32 bit word
			// no need to check for zero on the last byte either.
			return ntzTable[lower >>> 24] + 24;
		} else {
			// grab upper 32 bits
			int upper = (int) (val >> 32);
			lowByte = upper & 0xff;
			if (lowByte != 0)
				return ntzTable[lowByte] + 32;
			lowByte = (upper >>> 8) & 0xff;
			if (lowByte != 0)
				return ntzTable[lowByte] + 40;
			lowByte = (upper >>> 16) & 0xff;
			if (lowByte != 0)
				return ntzTable[lowByte] + 48;
			// no need to mask off low byte for the last byte in the 32 bit word
			// no need to check for zero on the last byte either.
			return ntzTable[upper >>> 24] + 56;
		}
	}

	/**
	 * returns 0 based index of first set bit
	 * (only works for x!=0)
	 */
	public static int ntz2(long x) {
		int n = 0;
		int y = (int) x;
		if (y == 0) {
			n += 32;
			y = (int) (x >>> 32);
		} // the only 64 bit shift necessary
		if ((y & 0x0000FFFF) == 0) {
			n += 16;
			y >>>= 16;
		}
		if ((y & 0x000000FF) == 0) {
			n += 8;
			y >>>= 8;
		}
		return (ntzTable[y & 0xff]) + n;
	}

	int ntz3(long x) {
		int n = 1;

		// do the first step as a long, all others as ints.
		int y = (int) x;
		if (y == 0) {
			n += 32;
			y = (int) (x >>> 32);
		}
		if ((y & 0x0000FFFF) == 0) {
			n += 16;
			y >>>= 16;
		}
		if ((y & 0x000000FF) == 0) {
			n += 8;
			y >>>= 8;
		}
		if ((y & 0x0000000F) == 0) {
			n += 4;
			y >>>= 4;
		}
		if ((y & 0x00000003) == 0) {
			n += 2;
			y >>>= 2;
		}
		return n - (y & 1);
	}

	/***
	 * Many 32 bit ntz algorithms at http://www.hackersdelight.org/HDcode/ntz.cc
	 */

	public Object clone() {
		try {
			OpenBitSet obs = (OpenBitSet) super.clone();
			obs.bits = obs.bits.clone(); // hopefully an array clone is as
											// fast(er) than arraycopy
			return obs;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	/** this = this AND other */
	public void intersect(OpenBitSet other) {
		int newLen = Math.min(this.wlen, other.wlen);
		long[] thisArr = this.bits;
		long[] otherArr = other.bits;
		// testing against zero can be more efficient
		int pos = newLen;
		while (--pos >= 0) {
			thisArr[pos] &= otherArr[pos];
		}
		if (this.wlen > newLen) {
			// fill zeros from the new shorter length to the old length
			Arrays.fill(bits, newLen, this.wlen, 0);
		}
		this.wlen = newLen;
	}

	/** this = this OR other */
	public void union(OpenBitSet other) {
		int newLen = Math.max(wlen, other.wlen);
		ensureCapacityWords(newLen);

		long[] thisArr = this.bits;
		long[] otherArr = other.bits;
		int pos = this.wlen;
		while (--pos >= 0) {
			thisArr[pos] |= otherArr[pos];
		}
		if (this.wlen < newLen) {
			System.arraycopy(otherArr, this.wlen, thisArr, this.wlen, newLen - this.wlen);
		}
		this.wlen = newLen;
	}

	/** Remove all elements set in other. this = this AND_NOT other */
	public void remove(OpenBitSet other) {
		int idx = Math.min(wlen, other.wlen);
		long[] thisArr = this.bits;
		long[] otherArr = other.bits;
		while (--idx >= 0) {
			thisArr[idx] &= ~otherArr[idx];
		}
	}

	/** this = this XOR other */
	public void xor(OpenBitSet other) {
		int newLen = Math.max(wlen, other.wlen);
		ensureCapacityWords(newLen);

		long[] thisArr = this.bits;
		long[] otherArr = other.bits;
		int pos = this.wlen;
		while (--pos >= 0) {
			thisArr[pos] ^= otherArr[pos];
		}
		if (this.wlen < newLen) {
			System.arraycopy(otherArr, this.wlen, thisArr, this.wlen, newLen - this.wlen);
		}
		this.wlen = newLen;
	}

	// some BitSet compatability methods

	// ** see {@link intersect} */
	public void and(OpenBitSet other) {
		intersect(other);
	}

	// ** see {@link union} */
	public void or(OpenBitSet other) {
		union(other);
	}

	// ** see {@link andNot} */
	public void andNot(OpenBitSet other) {
		remove(other);
	}

	/**
	 * Resize the bitset with the size given as a number of words (64
	 * bit longs)
	 */
	public void ensureCapacityWords(int numWords) {
		if (bits.length < numWords) {
			long[] newBits = new long[numWords];
			System.arraycopy(bits, 0, newBits, 0, wlen);
			bits = newBits;
		}
	}

	public void trimTrailingZeros() {
		int idx = wlen - 1;
		while (idx >= 0 && bits[idx] == 0)
			idx--;
		wlen = idx + 1;
	}

}
