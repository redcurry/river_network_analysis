import java.io.*;

public class ShortGrid
{

    public ShortGrid( String gridPathName, int rowsVal, int colsVal )
        throws FileNotFoundException
    {
        gridFile = new RandomAccessFile( gridPathName, "r" );

        rows = rowsVal;
        cols = colsVal;
    }

    public short get( int i, int j ) throws IOException
    {
        gridFile.seek( ( i * 2 * cols ) + ( j * 2 ) );

        byte[] values = new byte[2];
        gridFile.read( values, 0, 2 );

        return bytesToShort( values );
    }

    public static short bytesToShort( byte[] bytes )
    {
        return (short)( ( ( bytes[0] & 0xFF ) << 8  ) |
                          ( bytes[1] & 0xFF )       );
    }

    public void close() throws IOException
    {
        gridFile.close();
    }

    public RandomAccessFile gridFile;

    public int rows;
    public int cols;

}