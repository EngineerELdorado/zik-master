<?php
require_once('../includes/config.php');
	$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
	mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST)):
	$id = $_REQUEST['tid'];
	$qry = $conn->query("select count(*) as total_comments FROM comments where comments.tid = '$id'"); 
	$cnt =  $qry->fetch_assoc();
	$query = $conn->query("select comments.id as cid, comments.uid, comments.tid,comments.message, comments.time,users.username,users.image from comments join users on users.idu = comments.uid  where comments.tid ='$id' ORDER BY comments.time DESC ");
	$num = $query->num_rows;
	if($num >0) :
		while($row = $query->fetch_assoc()) :
			$res = array_merge($cnt,$row);
			$inf['comments'][] = $res;
			$inf['success'] =1;
		endwhile;
		else:
		$inf['comments'][] = 'No Comment Yet';
		$inf['success'] =0;
	endif;
	$val = json_encode($inf);
	echo $val;
endif;




?>