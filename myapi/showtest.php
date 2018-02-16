<?php 
require_once('../includes/config.php');
$db = new mysqli($CONF['host'], $CONF['user'], $CONF['pass'], $CONF['name']);
if(isset($_REQUEST['showtest']) && $_REQUEST['showtest']=='show' && $_REQUEST['uid']!='')
{
     $uid = $_REQUEST['uid'];

	$query = $db->query("select id from playlists where playlists.by = '$uid'");
   foreach($query as $val)
   {
	 $id = $val['id'];
	   $qry = $db->query("SELECT  COUNT( playlist ) AS total_tracks FROM playlistentries where playlist = '$id'     GROUP BY  playlist");
	   $qq[] = $qry;
   }

	

}
$val = json_encode($set);
   echo $val;
?>