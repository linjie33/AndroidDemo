var app = new Vue({
    el:'#app',
    data:{

        foodList:[
            {
                name:'米',
                price:10,
                discount:0.5,
            },
            {
                name:'面',
                price:5,
                discount:0.8
            },
            {
                name:'粉',
                price:20,
            },

        ],
        sportList:[
            {
                name:"篮球",
                num:10,
            },
            {
                name:"足球",
                num:22,
            },
        ],

    }
});