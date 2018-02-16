<?php 
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $db, 'utf8');
if(isset($_REQUEST['playlist']) && $_REQUEST['playlist']=='true' && $_REQUEST['uid']!='')
{
   $uid = $_REQUEST['uid'];
   $names = $_REQUEST['listname']; 
	$description = $_REQUEST['	description'];
	$access = $_REQUEST['access'];
	$query = $db->query("insert into playlists(playlists.by,playlists.name,playlists.description,playlists.public) value('$uid','$names','$description','$access')");
	if($query)
	{
		 $set['playlists'][]="add playlist successfully";
	}
	else
	{
	      	$set['playlists'][]="add to fail";
	}
}
	if(isset($_REQUEST['playlistentery']) && $_REQUEST['playlistentery']==true && $_REQUEST['trackid']!='')
	{
     	$playlistid = $_REQUEST['playlistid'];
		$trackid = $_REQUEST['trackid'];
		$query = $db->query("select * from playlistentries where playlistentries.playlist='$playlistid' && playlistentries.track='$trackid'");
		$num = $query->num_rows;
		if($num >0) 
		{
			$set['playlists-entery'][]='already exist';
			$set['success']=0;
			
	    }
		else 
		{
				$query = $db->query("insert into playlistentries(playlistentries.playlist,playlistentries.track) value('$playlistid','$trackid')");
	        if($query)
			{
				 $set['playlists-entery'][]="add playlist entey successfully";
				$set['success']=1;
			}
			else
			{
					$set['playlists-entery'][]="add to fail";
				   $set['success']=0;
			}
		}
}
if(isset($_REQUEST['showlist']) && $_REQUEST['showlist']=='show' && $_REQUEST['uid']!='')
{
     $uid = $_REQUEST['uid'];

	//$qry = $db->query("select  count(playlistentries.playlist) as playlistt from playlistentries join playlists on playlists.id = playlistentries.playlist where playlists.by = '$uid' group by playlist");
	
	$query = $db->query("select  distinct playlists.id,`playlists`.*,ifnull(tracks.art,'default.png')  as image,count(playlist) as total_tracks from playlistentries left join tracks on tracks.id = playlistentries.track right join playlists on playlists.id = playlistentries.playlist where playlists.by='$uid' group by playlistentries.playlist,playlists.id order by playlists.id desc");
	$num = $query->num_rows;
	if($num >0)
	{
	   while($row = $query->fetch_assoc())
	   {
           
		  //$count = count($row['playlist']);
		   $set['show-list'][] =$row;
		   $set['success']=1;
	   }
	}
	else
	{
	    $set['show-list'][]='no list';	
		$set['success']=0;
	}
}

if(isset($_REQUEST['totaltrack']) && $_REQUEST['totaltrack']=='true')
{
     $uid = $_REQUEST['uid'];

	
	$query =  $db->query("SELECT  playlist,COUNT( playlist ) AS total_tracks FROM playlistentries   GROUP BY  playlist");
	$num = $query->num_rows;
	if($num >0)
	{
	   while($row = $query->fetch_assoc())
	   {
          
		   $set['show-list'][] =$row;
		   $set['success']=1;
	   }
	}
	else
	{
	    $set['show-list'][]='no list';	
		$set['success']=0;
	}
}

if(isset($_REQUEST['playlistRemove']) && $_REQUEST['playlistRemove']=='true' && $_REQUEST['playlistid']!='')
{
	$playlistid = $_REQUEST['playlistid'];
	$query = $db->query("delete from playlists where playlists.id ='$playlistid'");
	$query = $db->query("delete from playlistentries where playlistentries.playlist='$playlistid'");
	if($query)
	{
		$set['list-remove'][] = 'delete successfully';
	}
	else
	{
		$set['list-remove'][]='No Delete';
	}
}
if(isset($_REQUEST['playlists']) && $_REQUEST['playlists']=='yes' && $_REQUEST['listid']!='')
{
	$listid = $_REQUEST['listid'];
	$query = $db->query("select `tracks`.*,users.first_name,users.last_name from playlistentries join tracks on tracks.id = playlistentries.track join users on users.idu = tracks.uid where tracks.public = '1' && playlistentries.playlist = '$listid'");
	$num = $query->num_rows;
	if($num>0)
	{
		while($row = $query->fetch_object())
		{
			 $row = array('id'=>$row->id,'title'=>html_entity_decode($row->title),'description'=>$row->description,'uid'=>$row->uid,'first_name'=>$row->first_name,'last_name'=>$row->last_name,'name'=>$row->name,'tag'=>$row->tag,'art'=>$row->art,'buy'=>$row->buy,'record'=>$row->record,'release'=>$row->release,'license'=>$row->license,'size'=>$row->size,'download'=>$row->download,'time'=>$row->time,'public'=>$row->public,'likes'=>$row->likes,'downloads'=>$row->likes,'views'=>$row->views);
			$set['play-list'][] = $row;
			$set['success']=1;
		}
	}
		else
		{
		   $set['play-list'][] = 'no-playlist';
		  $set['success']=0;
		}
	
}
if(isset($_REQUEST['trackRemove']) && $_REQUEST['trackRemove']=='true' && $_REQUEST['trackid']!='')
{
	       $trckid     =     $_REQUEST['trackid'];
	       $listid     =     $_REQUEST['listid'];
	  $query = $db->query("delete from playlistentries where playlist = '$listid' && track = '$trckid'");
	if($query)
	{
		$set['trackremove'][]='Track Remove';
		$set['success'] = 1;
	}
	else
	{
	   $set['trackremove'][]='Track Remove Fail';
		   $set['success']=0;
	}
}
	$val = json_encode($set,JSON_HEX_AMP);
   echo $val;

?>