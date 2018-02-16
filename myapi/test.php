<?php 
require_once('../includes/config.php');
$conn = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
 
mysqli_set_charset( $conn, 'utf8');
if(isset($_REQUEST)):
$ids = $_REQUEST['id'];
$title = $_REQUEST['title'];
$tags = $_REQUEST['tag'];

    $qry1 =  $conn->query("insert into tracks (title) values('$title')");
		

 endif;
?>