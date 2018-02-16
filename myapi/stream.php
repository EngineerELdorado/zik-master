<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST)) :
    $id = $_REQUEST['idu']; 
    $query = $conn->query("select tracks.id,tracks.title,tracks.description,tracks.uid,users.first_name,users.last_name,users.username,tracks.name,tracks.tag,tracks.art,tracks.buy,tracks.record,tracks.release,tracks.license,tracks.size,tracks.download,tracks.time,tracks.public,tracks.likes,tracks.downloads,tracks.views,relations.subscriber,relations.leader from tracks join users on users.idu = tracks.uid join relations on relations.leader = users.idu || relations.subscriber = users.idu where relations.subscriber = '$id' && tracks.public = '1' GROUP BY tracks.id ORDER BY tracks.id DESC");
    $num = $query->num_rows;   
	if($num >0) :
		while($row = $query->fetch_object()) :      
			  
			$set['stream'][] = $row;
			$set['success'] = 1;

			endwhile; 
		else :
		   $set['stream'][] = 'no-tracks';
		   $set['success']=0;
		endif;
			 $val = json_encode($set,JSON_HEX_AMP);
		echo $val; 
	endif;
?>