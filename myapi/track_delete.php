<?php
require_once('../includes/config.php');
	$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
	mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['tid'])):
	$tid = $_REQUEST['tid'];
	//$uid = $_REQUEST['uid'];
	 $result = $conn->query("select * from tracks where id = '$tid'");
     $num = $result->num_rows;
       if($num > 0) :
         $row = $result->fetch_object();
         if($row->art != '') : 
               unlink('../uploads/media/'.$row->art);
         	endif;
         if($row->name != '') : 
               unlink('../uploads/tracks/'.$row->name);
         	endif;
	endif;
	$qry = $conn->query("delete FROM comments where comments.tid = '$tid'"); 
	$qry1 = $conn->query("delete FROM likes where likes.track = '$tid'"); 
	$qry2 = $conn->query("delete FROM tracks where tracks.id = '$tid'"); 
	$query = $conn->query("delete from notifications where notifications.parent = '$tid'");
	
	if($query) :
		$inf['tracks'] = 'tracks deleted..';
		$inf['success'] =1;
	else:
		$inf['tracks'] = 'tracks not deleted..';
		$inf['success'] =0;
	endif;
	$val = json_encode($inf);
	echo $val;
endif;




?>