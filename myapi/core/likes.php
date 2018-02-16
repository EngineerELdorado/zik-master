<?php 
require_once "config.php";

class likes extends config 
{
	 
	protected $response;
     function __construct()
	 {
		  parent::__construct();
	 }
	  
	  
	 function do_like()
	 {
		
	      echo  $this->save_like('likes','time');
           
		
	 }
	
	function push()
	{
		 
		  return "send";
	}
}
?>