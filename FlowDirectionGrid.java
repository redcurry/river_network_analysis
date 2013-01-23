import java.io.*;

public class FlowDirectionGrid
{

    public FlowDirectionGrid( String name, int r, int c ) throws IOException
    {
        _gridFile = new RandomAccessFile( name, "r" );

        _rows = r;
        _cols = c;
    }

    public int get( Point p ) throws IOException
    {
        return get( p.getI(), p.getJ() );
    }

    public int get( int i, int j ) throws IOException
    {
        // Return the short integer at the specifed grid location
        return (int)_readShort( _computeFileLocation( i, j ) );
    }

    public void close() throws IOException
    {
        _gridFile.close();
    }

    private short _readShort( long loc ) throws IOException
    {
        // Move the file pointer to the specified location
        _gridFile.seek( loc );

        // Read two bytes from the grid
        byte[] bytes = new byte[2];
        _gridFile.read( bytes, 0, 2 );

        // Return the bytes as an short integer
        return _bytesToShort( bytes );
    }

    private long _computeFileLocation( int i, int j )
    {
        // Return the file location corresponding
        // to the specified grid location
        return i * 2 * _cols + j * 2;
    }

    private short _bytesToShort( byte[] bytes )
    {
        // Return the short integer represented by the two bytes
        return (short)( ( ( bytes[0] & 0xFF ) << 8  ) |
                          ( bytes[1] & 0xFF )       );
    }

    public static final int DIRECTION_NONE = 0;
    public static final int DIRECTION_E    = 1;
    public static final int DIRECTION_SE   = 2;
    public static final int DIRECTION_S    = 4;
    public static final int DIRECTION_SW   = 8;
    public static final int DIRECTION_W    = 16;
    public static final int DIRECTION_NW   = 32;
    public static final int DIRECTION_N    = 64;
    public static final int DIRECTION_NE   = 128;

    private RandomAccessFile _gridFile;

    private int _rows;      // Number of rows in the flow direction grid
    private int _cols;      // Number of columns in the flow direction grid

}