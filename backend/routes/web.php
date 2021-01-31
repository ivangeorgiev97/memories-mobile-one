<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It is a breeze. Simply tell Lumen the URIs it should respond to
| and give it the Closure to call when that URI is requested.
|
*/

$router->get('/', function () use ($router) {
    return $router->app->version();
});

$router->group(['prefix' => 'api'], function () use ($router) {
    // Categories
    $router->get('categories/getAll', ['uses' => 'CategoryController@getAll']);
    $router->get('categories/getById/{id}', ['uses' => 'CategoryController@getById']);
    $router->post('categories/create', ['uses' => 'CategoryController@create']);
    $router->put('categories/update/{id}', ['uses' => 'CategoryController@update']);
    $router->delete('categories/delete/{id}', ['uses' => 'CategoryController@delete']);

    // Memories
    $router->get('memories/getAll', ['uses' => 'MemoryController@getAll']);
    $router->get('memories/getById/{id}', ['uses' => 'MemoryController@getById']);
    $router->post('memories/create', ['uses' => 'MemoryController@create']);
    $router->put('memories/update/{id}', ['uses' => 'MemoryController@update']);
    $router->delete('memories/delete/{id}', ['uses' => 'MemoryController@delete']);
});
