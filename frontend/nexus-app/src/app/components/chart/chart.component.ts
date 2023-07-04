import { Component, Input, SimpleChanges } from '@angular/core';
import { Tweet } from 'app/models/tweet.model';
import { TweetService } from 'app/services/tweet.service';
import { ChartOptions, ChartType, ChartDataset, elements, Chart } from 'chart.js';


@Component({
  selector: 'app-bar-chart',
  templateUrl: './chart.component.html',
  styleUrls: ['./chart.component.css']
})
export class ChartComponent {
  public barChartOptions: ChartOptions = {
    responsive: true, 
    maintainAspectRatio: false,
    scales: {
      x: {
        grid: {
          display: false
        },
        beginAtZero: true
      },
      y: {
        grid: {
          display: true
        },
          beginAtZero: true 

      }
    },
    plugins: {
      legend: {
        display: false 
      }
    },
    elements: {
      bar: {
        backgroundColor: ["red", "green", "blue", "orange", "brown"],
        
      }
    }
  };
  
  
  public barChartLegend = false;

  data?: number[]
  tweets?: Tweet[]

  constructor(private tweetService: TweetService){
    
  }
  ngOnInit(): void{
    this.tweetService.getMostLikedTweets(0, 6).subscribe(
      receivedTweets => {this.tweets = receivedTweets; this.defineChart()}
    )
  }

  defineChart(){

    const xValues: string[] = []
    const yValues: number[] = []

    this.tweets?.forEach(tweet => {
      xValues.push(`'${tweet.text}'\nby: ${tweet.author}`)
      yValues.push(tweet.likes.length)
    });


    var myChart =
        new Chart("myChart", {
          type: "bar",
          data: {
            labels: xValues,
            datasets: [{
              data: yValues
            }]
          },
          options: {
            scales: {
              y:{

              }
            }
          }
        });
  }


  public barChartData: ChartDataset[] = [];

  chartClicked(e: any): void {
    // Handle chart click event
  }

  chartHovered(e: any): void {
    // Handle chart hover event
  }
}
