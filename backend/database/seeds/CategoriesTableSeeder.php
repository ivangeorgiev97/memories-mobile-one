
<?php

use Illuminate\Database\Seeder;
use App\Category as CategoryModel;

class CategoriesTableSeeder extends Seeder
{
    /**
     * Run the database seeds.
     *
     * @return void
     */
    public function run()
    {
        $categories = [
            [
                'name' => 'Category 1'
            ],
            [
                'name' => 'Category 2'
            ]
        ];

        foreach ($categories as $category) {
            CategoryModel::create($category);
        }
    }
}
