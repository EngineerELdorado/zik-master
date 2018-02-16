<?php 
include "config.php";


class controller extends config 
{
	    function __construct()
		{
		      parent::__construct();	
		}
	
	    function do_all()
		{
			$this->byAction();	
		}
        
	    function registerTOKEN()
		{
			$qr = $this->db->query("select * from device_register where user_id =".$_REQUEST['user_id']); 
			if($qr->num_rows > 0):
			$this->db->query("update `device_register` set `device_token`='".$_REQUEST['device_token']."' where user_id ='".$_REQUEST['user_id']."' ");
			else:
			$date = date('Y-m-d');
			$data = array_merge($_REQUEST,array('created_at'=>$date));
			$keys = implode(",",array_keys($data));
			$this->db->query("insert into device_register($keys) value('".implode("','",$data)."')");
			endif;
			
		}
	    
	    function chitchat()
		{
		    $this->chats();	
		}
	    
	    function setTrack()
		{
			 $this->uploadtrack();	
		}
	     
	    function friends()
		{
			$this->addtofriend();
		}
	
	    function testApp()
		{
		       $this->tk();	
		}
	
	   function tokenreload()
	   {
		 $this->tokenreset();   
	   }
}
?>