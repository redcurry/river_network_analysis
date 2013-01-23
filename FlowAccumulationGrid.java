import java.io.*;

public class FlowAccumulationGrid
{

    public FlowAccumulationGrid( String name, int r, int c ) throws IOException
    {
        _gridFile = new RandomAccessFile( name, "r" );

        _rows = r;
        _cols = c;

        _cutoff = 0;
    }

    public void setCutoff( int cutoff )
    {
        _cutoff = cutoff;
    }

    public int get( Point p ) throws IOException
    {
        return get( p.getI(), p.getJ() );
    }

    public int get( int i, int j ) throws IOException
    {
        // Read the integer at the specifed grid location
        int value = _readInteger( _computeFileLocation( i, j ) );

        // Is the value between 0 and the cutoff?
        if( 0 < value && value < _cutoff )
        {
            // Return no flow accumulation
            return 0;
        }
        else
        {
            // Return the flow accumulation value
            return value;
        }
    }

    public void close() throws IOException
    {
        _gridFile.close();
    }

    private int _readInteger( long loc ) throws IOException
    {
        // Move the file pointer to the specified location
        _gridFile.seek( loc );

        // Read four bytes from the grid
        byte[] bytes = new byte[4];
        _gridFile.read( bytes, 0, 4 );

        // Return the bytes as an integer
        return _bytesToInteger( bytes );
    }

    private long _computeFileLocation( int i, int j )
    {
        // Return the file location corresponding
        // to the specified grid location
        return i * 4 * _cols + j * 4;
    }

    private int _bytesToInteger( byte[] bytes )
    {
        // Return the integer represented by the four bytes
        return ( ( bytes[0] & 0xFF ) << 24 ) |
               ( ( bytes[1] & 0xFF ) << 16 ) |
               ( ( bytes[2] & 0xFF ) << 8  ) |
               (   bytes[3] & 0xFF         );
    }

    private RandomAccessFile _gridFile;

    private int _rows;      // Number of rows in the flow accumulation grid
    private int _cols;      // Number of columns in the flow accumulation grid

    private int _cutoff;    // Flow accumulation cutoff value

}