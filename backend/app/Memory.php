<?php

namespace App;

use Illuminate\Database\Eloquent\Model;

class Memory extends Model 
{
    protected $fillable = [
        'id', 'title', 'description', 'category_id'
    ];

    protected $hidden = [];


    public function category() 
    {
        return $this->belognsTo(Category::class);
    }
}
