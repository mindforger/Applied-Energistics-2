package appeng.debug;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.core.CommonHelper;
import appeng.tile.AEBaseTile;
import appeng.tile.events.AETileEventHandler;
import appeng.tile.events.TileEventType;
import appeng.util.Platform;

public class TileCubeGenerator extends AEBaseTile
{

	int size = 3;
	ItemStack is = null;
	int countdown = 20 * 10;
	EntityPlayer who;
	
	class TCG extends AETileEventHandler {
		
		public TCG() {
			super(TileEventType.TICK);
		}
		
		@Override
		public void Tick()
		{
			if ( is != null && Platform.isServer() )
			{
				countdown--;
				
				if (countdown % 20 == 0 )
				{
					for ( EntityPlayer e : CommonHelper.proxy.getPlayers() )
					{
						e.addChatMessage(new ChatComponentText("Spawning in... "+( countdown / 20 )));
					}
				}
				
				if ( countdown <= 0 )
					spawn();
			}
		}
		
	};
	
	void spawn()
	{
		worldObj.setBlock( xCoord, yCoord, zCoord, Platform.air, 0, 3 );
		
		Item i = is.getItem();
		int side = ForgeDirection.UP.ordinal();
		
		int half = (int) Math.floor( size / 2 );
		
		for ( int y = 0; y < size; y++ )
		{
			for ( int x = -half; x < half; x++ )
			{
				for ( int z = -half; z < half; z++ )
				{
					i.onItemUse( is.copy(), who, worldObj, x+xCoord, y+yCoord-1, z+zCoord, side, 0.5f, 0.0f, 0.5f );
				}
			}
		}
	}
	
	public TileCubeGenerator() {
		addNewHandler( new TCG() );
	}
	
	public void click(EntityPlayer player)
	{
		if (  Platform.isServer() )
		{
			ItemStack hand = player.inventory.getCurrentItem();
			who = player;
			
			if ( hand == null )
			{
				is = null;
				
				if ( player.isSneaking() )
					size --;
				else
					size++;
				
				if ( size < 3 )
					size = 3;
				if ( size > 64 )
					size = 64;
				
				player.addChatMessage( new ChatComponentText("Size: "+size) );
			}
			else
			{
				countdown = 20 * 10;
				is = hand;
			}
		}
	}

}
