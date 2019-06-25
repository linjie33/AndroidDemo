var app = new Vue({
    el:'#app',
    data:{
        name:"abc",
        age:22,
        sex:"MALE",
    },
    methods:{
        onClick:function(){
            console.log('has clicked');
        },
        onEnter:function(){
            console.log('mouse enter');
        },
        onLeave:function(){
            console.log('mouse leave');
        },
        onInto:function(){
            console.log('Into this button');
        },
        onOut:function(){
            console.log('Out this button');
        },
        onSubmit:function(e){
            e.preventDefault();
            console.log('has submitted');
        },
        onEnter:function(){
            console.log('entered');
        }
    }

});