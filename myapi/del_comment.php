<?php
require_once('../includes/config.php');
	$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
	mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['tid'])):
	$tid = $_REQUEST['tid'];
	$uid = $_REQUEST['uid'];
	$cid = $_REQUEST['cid'];
	
	$qry1 =  $conn->query("select * from comments where comments.id = '$cid'");
	$qry = $conn->query("delete FROM comments where comments.tid = '$tid' && comments.uid = '$uid' && comments.id = '$cid'"); 
	$res =  $qry1->fetch_assoc();
	$tm = $res ['time'];
	
	$query = $conn->query("delete from notifications where notifications.from = '$uid' && notifications.parent = '$tid' && notifications.time = '$tm'");
	
	
	if($query) :
		$inf['comments'][] = 'comment deleted..';
		$inf['success'] =1;
	else:
		$inf['comments'][] = 'Comment not deleted..';
		$inf['success'] =0;
	endif;
	$val = json_encode($inf);
	echo $val;
endif;




?>