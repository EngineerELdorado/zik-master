<?php
require_once('../includes/config.php');
	$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST['tid']) && $_REQUEST['uid'] && $_REQUEST['msg']):
	$tid = $_REQUEST['tid'];
	$idu = $_REQUEST['idu'];
	$uid = $_REQUEST['uid'];
	$message = $_REQUEST['msg'];
	$qry = $conn->query("insert into notifications (notifications.from,notifications.to,notifications.parent,notifications.child,notifications.type,notifications.read)values('$idu','$uid','$tid','','1','0')");   
	$query = $conn->query("insert into comments (comments.uid,comments.tid,comments.message)values('$idu','$tid','$message')");
	if($query) :
		$inf['comments added'][] = 'Successfull';
		$inf['success'] =1;
    else:
		$inf['comments'][] = 'comment not added';
		$inf['success'] =0;
	endif;
		$val = json_encode($inf);
		echo $val;
endif;



?>