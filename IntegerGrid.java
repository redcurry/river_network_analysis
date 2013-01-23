import java.io.*;

public class IntegerGrid
{

    public IntegerGrid( String gridPathName, int rowsVal, int colsVal )
        throws FileNotFoundException
    {
        gridFile = new RandomAccessFile( gridPathName, "rw" );

        rows = rowsVal;
        cols = colsVal;
    }

    public int get( Point p ) throws IOException
    {
        return get( p.i, p.j );
    }

    public int get( int i, int j ) throws IOException
    {
        gridFile.seek( ( i * 4 * cols ) + ( j * 4 ) );

        byte[] values = new byte[4];
        gridFile.read( values, 0, 4 );

        int value = bytesToInteger( values );

        if( value < 10000 )
        {
            if( value == -9999 )
            {
                return value;
            }
            else
            {
                return 0;
            }
        }

        return value;
    }

    public void set( int i, int j, int value ) throws IOException
    {
        gridFile.seek( ( i * 4 * cols ) + ( j * 4 ) );
        gridFile.write( integerToBytes( value ) );
    }

    public int bytesToInteger( byte[] bytes )
    {
        return ( ( bytes[0] & 0xFF ) << 24 ) |
               ( ( bytes[1] & 0xFF ) << 16 ) |
               ( ( bytes[2] & 0xFF ) << 8  ) |
               (   bytes[3] & 0xFF         );
    }

    public byte[] integerToBytes( int value )
    {
        byte[] values = new byte[4];

        for (int i = 0; i < 4; i++ )
        {
            values[i] = (byte)( (value >> (i * 8) ) % 0xFF);
        }

        return values;
    }

    public void close() throws IOException
    {
        gridFile.close();
    }

    public RandomAccessFile gridFile;

    public int rows;
    public int cols;

}