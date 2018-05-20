;(function($){

	var settings = {
		shrinktogglerAfter: 'firstpage', // shrink hamburgerui UL to just show last LI with hamburger icon when user scrolls the page down? 'firstpage' or px number (ie: 200)
		dismissmenuDelay: 200 // delay in miliseconds after user clicks on full screen menu before hiding it
	}

	function intializeMenu(){
		var $menuwrapper = $('#hamburgericonmenuwrapper')
		var $fullscreenmenu = $menuwrapper.find('#fullscreenmenu')
		var $hamburgerui = $('#hamburgerui')
		var $toggler = $('#navtoggler').parent()
		var scrolltop
		var strinkafter
		var shrinktimer
		var dismisstimer
	
		$toggler.on('click', function(e){
			$menuwrapper.toggleClass('open')
			e.preventDefault()
		})

		$fullscreenmenu.on('click', function(e){
			clearTimeout(dismisstimer)
			dismisstimer = setTimeout(function(){
				$menuwrapper.removeClass('open')				
			}, settings.dismissmenuDelay)
		})

		if ($menuwrapper.length == 1 && settings.shrinktogglerAfter != 0){
			var shrinktogglerAfter = settings.shrinktogglerAfter
			$(window).on('scroll resize', function(e){
				clearTimeout(shrinktimer)
				shrinktimer = setTimeout(function(){
					scrolltop = $(window).scrollTop()
					strinkafter = (shrinktogglerAfter == 'firstpage')? $(window).height() : parseInt(shrinktogglerAfter)
					if ( scrolltop > strinkafter && !$hamburgerui.hasClass('shrink') ){
						$hamburgerui.addClass('shrink')
					}
					else if ( scrolltop < strinkafter && $hamburgerui.hasClass('shrink') ){
						$hamburgerui.removeClass('shrink')
					}
				}, 50)
			})
		}
	}

	$(function(){ // on DOM load
		intializeMenu()
	})

}(jQuery))